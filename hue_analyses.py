import cv2
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
from datetime import datetime
import os
from ultralytics import YOLO
from collections import defaultdict

def analyze_hue_with_yolo(video_path, output_dir="hue_analysis_yolo_results", smooth_window=5, yolo_model="yolov8n.pt"):
    """Enhanced version with visual bounding boxes and flicker analysis."""
    
    os.makedirs(output_dir, exist_ok=True)
    video_name = os.path.splitext(os.path.basename(video_path))[0]
    
    # Load YOLO model
    model = YOLO(yolo_model)
    
    # Open video
    cap = cv2.VideoCapture(video_path)
    if not cap.isOpened():
        print(f"Error opening video: {video_path}")
        return
    
    fps = cap.get(cv2.CAP_PROP_FPS)
    total_frames = int(cap.get(cv2.CAP_PROP_FRAME_COUNT))
    width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
    height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
    
    # Setup video writer for visualization
    fourcc = cv2.VideoWriter_fourcc(*'mp4v')
    out_video = cv2.VideoWriter(
        f"{output_dir}/{video_name}_analysis_visual.mp4", 
        fourcc, fps, (width, height)
    )

    print(f"\nAnalyzing: {video_name} | Frames: {total_frames} | FPS: {fps:.1f}")

    # Storage for analysis
    frame_data = []
    hue_variances = []
    flicker_scores = defaultdict(int)
    prev_boxes = defaultdict(lambda: None)
    object_appearances = defaultdict(int)  # Track how many frames each object appears

    for frame_num in range(total_frames):
        ret, frame = cap.read()
        if not ret:
            break

        # Run YOLO detection
        results = model.track(frame, persist=True, verbose=False)
        boxes = results[0].boxes.xywh.cpu().numpy()
        track_ids = results[0].boxes.id.int().cpu().numpy() if results[0].boxes.id is not None else []

        # Convert to HSV
        hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
        hue = hsv[:, :, 0]
        vis_frame = frame.copy()  # Frame for visualization

        for i, box in enumerate(boxes):
            x_center, y_center, w, h = box
            x1, y1 = int(x_center - w/2), int(y_center - h/2)
            x2, y2 = int(x_center + w/2), int(y_center + h/2)
            
            # Extract hue stats
            hue_roi = hue[y1:y2, x1:x2]
            avg_hue = np.mean(hue_roi)
            hue_var = np.var(hue_roi)
            
            # Track object
            track_id = track_ids[i] if i < len(track_ids) else i
            current_bbox = (x1, y1, x2, y2)
            object_appearances[track_id] += 1

            # Flicker detection
            if prev_boxes[track_id] is not None:
                iou = calculate_iou(prev_boxes[track_id], current_bbox)
                if iou < 0.5:  # Threshold for flicker
                    flicker_scores[track_id] += 1
            
            prev_boxes[track_id] = current_bbox

            # Store data
            frame_data.append({
                "frame": frame_num,
                "time_s": frame_num / fps,
                "track_id": track_id,
                "avg_hue": avg_hue,
                "hue_variance": hue_var,
                "bbox": current_bbox
            })

            # Visualize bounding boxes
            is_flickering = flicker_scores.get(track_id, 0) / object_appearances[track_id] > 0.3
            box_color = (0, 0, 255) if is_flickering else (0, 255, 0)  # Red=flickering, Green=stable
            cv2.rectangle(vis_frame, (x1, y1), (x2, y2), box_color, 2)
            
            # Add info text
            info_text = f"ID:{track_id} H:{avg_hue:.1f}° {'(F)' if is_flickering else ''}"
            cv2.putText(vis_frame, info_text, (x1, y1-10), 
                        cv2.FONT_HERSHEY_SIMPLEX, 0.5, box_color, 1)

        # Write frame to output video
        out_video.write(vis_frame)
        
        # Global hue variance
        hue_variances.append(np.var(hue))

    cap.release()
    out_video.release()

    # Post-processing
    df = pd.DataFrame(frame_data)
    df['hue_smoothed'] = df.groupby('track_id')['avg_hue'].transform(
        lambda x: x.rolling(window=smooth_window, center=True).mean()
    )

    # Calculate normalized flicker scores
    flicker_df = pd.DataFrame({
        "track_id": list(flicker_scores.keys()),
        "flicker_count": list(flicker_scores.values())
    })
    flicker_df["flicker_score"] = flicker_df["flicker_count"] / df.groupby("track_id").size().loc[flicker_df["track_id"]].values

    # Save results
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    base_filename = f"{output_dir}/{video_name}_hue_yolo_{timestamp}"

    # Enhanced outputs
    df.to_csv(f"{base_filename}_per_object.csv", index=False)
    flicker_df.to_csv(f"{base_filename}_flicker_scores.csv", index=False)
    pd.DataFrame({"frame": range(total_frames), "global_hue_variance": hue_variances}).to_csv(
        f"{base_filename}_global_hue_variance.csv", index=False)

    # Filter LED-specific data
    led_objects = flicker_df[
        (flicker_df['flicker_score'] > 0.3) | 
        (df['avg_hue'].between(0, 180))
    ]
    led_objects.to_csv(f"{base_filename}_leds.csv", index=False)

    # Plotting
    plt.figure(figsize=(15, 8))
    for track_id, group in df.groupby("track_id"):
        plt.plot(group["time_s"], group["avg_hue"], label=f"Object {track_id}", alpha=0.7)
    
    plt.title(f"Hue Analysis with YOLO Tracking\n{video_name}")
    plt.xlabel("Time (s)")
    plt.ylabel("Average Hue (0-180)")
    plt.legend()
    plt.grid(True, alpha=0.3)
    plt.savefig(f"{base_filename}_hue_plot.png", dpi=150, bbox_inches="tight")
    plt.close()

    print(f"\nAnalysis complete! Key outputs:")
    print(f"- Visual analysis video: {output_dir}/{video_name}_analysis_visual.mp4")
    print(f"- LED-specific data: {base_filename}_leds.csv")
    print(f"- Flicker scores: {base_filename}_flicker_scores.csv")
    print(f"- Hue trends plot: {base_filename}_hue_plot.png")

    return df, flicker_df

def calculate_iou(box1, box2):
    """Compute Intersection-over-Union (IoU) between two bounding boxes."""
    x1_1, y1_1, x2_1, y2_1 = box1
    x1_2, y1_2, x2_2, y2_2 = box2

    # Intersection area
    xi1 = max(x1_1, x1_2)
    yi1 = max(y1_1, y1_2)
    xi2 = min(x2_1, x2_2)
    yi2 = min(y2_1, y2_2)
    inter_area = max(0, xi2 - xi1) * max(0, yi2 - yi1)

    # Union area
    box1_area = (x2_1 - x1_1) * (y2_1 - y1_1)
    box2_area = (x2_2 - x1_2) * (y2_2 - y1_2)
    union_area = box1_area + box2_area - inter_area

    return inter_area / union_area if union_area > 0 else 0

if __name__ == "__main__":
    import argparse
    parser = argparse.ArgumentParser(description="Analyze hue with YOLO object tracking and flicker detection")
    parser.add_argument("video_path", help="Path to video file")
    parser.add_argument("--smooth", type=int, default=5, help="Smoothing window size (odd number)")
    parser.add_argument("--model", default="yolov8n.pt", help="YOLO model path (e.g., yolov8s.pt)")
    args = parser.parse_args()

    analyze_hue_with_yolo(args.video_path, smooth_window=args.smooth, yolo_model=args.model)