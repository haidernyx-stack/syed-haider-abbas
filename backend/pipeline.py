import os
import subprocess
import logging

logger = logging.getLogger("ClipForgeFFmpeg")

class VideoPipelineEditor:
    """
    Python-FFmpeg automated processor.
    Exposes high-speed wrappers for audio extraction, timestamp chunk cutting,
    9:16 vertical cropping with face tracker coordinates, and captions styling.
    """

    @staticmethod
    def extract_audio(video_path: str, output_audio_path: str) -> bool:
        """
        Extracts a clean mono 16kHz audio track from input logs, fully optimized for Whisper transcription layers.
        """
        logger.info(f"Extracting audio track from {video_path} -> {output_audio_path}")
        
        # FFmpeg: mute video output, force mono, sample rate 16000Hz, override existing
        cmd = [
            "ffmpeg", "-y",
            "-i", video_path,
            "-vn",
            "-acodec", "pcm_s16le",
            "-ar", "16000",
            "-ac", "1",
            output_audio_path
        ]
        
        try:
            result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, check=True)
            logger.info("Successfully extracted mono track.")
            return True
        except subprocess.CalledProcessError as e:
            logger.error(f"FFmpeg audio extraction error: {e.stderr.decode()}")
            return False

    @staticmethod
    def cut_time_clip(video_path: str, output_path: str, start_sec: float, end_sec: float) -> bool:
        """
        Slices an exact time block of raw video without transcoding where possible, keeping system speed fast.
        """
        logger.info(f"Cutting clip timeline {start_sec}s to {end_sec}s from {video_path}")
        duration = end_sec - start_sec
        
        cmd = [
            "ffmpeg", "-y",
            "-ss", str(start_sec),
            "-i", video_path,
            "-t", str(duration),
            "-c", "copy",  # Fast stream copy without full frame rendering
            output_path
        ]
        
        try:
            subprocess.run(cmd, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            return True
        except subprocess.CalledProcessError as e:
            logger.error(f"FFmpeg trim timeline failure: {e.stderr.decode()}")
            return False

    @staticmethod
    def reframe_to_vertical(video_path: str, output_path: str, crop_center_pct: float = 0.5) -> bool:
        """
        Converts generic landscape 1080p (16:9) video grids into vertical 9:16 (1080x1920) assets.
        Applies a sliding focus window centring around face/motion coordinates.
        """
        logger.info(f"Reframing landscape video source to vertical. Face Tracking focus at {crop_center_pct * 100}%")
        
        # Crop formula: height stays input height. Width is input_height * 9/16.
        # X position is calculated dynamically relative to crop center focus percentage.
        vf_filter = (
            f"crop=h=in_h:w=in_h*9/16:x='(in_w-out_w)*{crop_center_pct}':y='(in_h-out_h)/2'"
        )
        
        cmd = [
            "ffmpeg", "-y",
            "-i", video_path,
            "-vf", vf_filter,
            "-c:v", "libx264",
            "-preset", "veryfast",
            "-crf", "22",
            "-c:a", "copy",
            output_path
        ]
        
        try:
            subprocess.run(cmd, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            return True
        except subprocess.CalledProcessError as e:
            logger.error(f"FFmpeg reframing crop error: {e.stderr.decode()}")
            return False

    @staticmethod
    def burn_styled_captions(video_path: str, srt_path: str, output_path: str, style_mode: str = "TikTok") -> bool:
        """
        Burns active srt word structures directly onto the vertical MP4 track.
        Applies custom subtitle font matching to correspond with style requests.
        """
        logger.info(f"Burning subtitles: {srt_path} onto {video_path} using format: {style_mode}")
        
        # Standard typography configs depending on styling
        if style_mode == "TikTok":
            style_filter = "Fontname=Impact,Fontsize=18,PrimaryColour=&H00FFFF&,OutlineColour=&H000000&,BorderStyle=3,Outline=2"
        elif style_mode == "Hormozi":
            style_filter = "Fontname=Arial,Fontsize=22,PrimaryColour=&HFFCC00&,Outline=1,Alignment=2,Bold=1"
        elif style_mode == "MrBeast":
            style_filter = "Fontname=Comic Sans MS,Fontsize=20,PrimaryColour=&HFF3399&,BackColour=&H000000&,BorderStyle=4"
        else:
            style_filter = "Fontname=Helvetica,Fontsize=15,PrimaryColour=&HFFFFFF&,Alignment=2"

        # Apply FFmpeg subtitle filters
        cmd = [
            "ffmpeg", "-y",
            "-i", video_path,
            "-vf", f"subtitles={srt_path}:force_style='{style_filter}'",
            "-c:v", "libx264",
            "-c:a", "copy",
            output_path
        ]
        
        try:
            subprocess.run(cmd, check=True, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            return True
        except subprocess.CalledProcessError as e:
            logger.error(f"FFmpeg subtitle burn failed: {e.stderr.decode()}")
            return False
