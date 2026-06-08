# ClipForge AI - Full-Stack Production Deployment Guide

This guide details steps for deploying **ClipForge AI** to production cloud instances (Ubuntu LTS 22.04+ on AWS EC2, GCP Compute Engine, DigitalOcean Droplet, or similar self-managed servers).

---

## 1. System Requirements

ClipForge AI runs heavy compute pipelines because of video decoding (FFmpeg) and Whisper speech transcribers.
* **CPU:** 4+ Cores (recommended x86_64 architecture)
* **RAM:** 8GB Minimum (16GB recommended if loading models natively)
* **GPU:** Optional but highly recommended (NVIDIA Tesla T4 / L4 for CUDA hardware-accelerated Whisper transcribing and reframing runs)

---

## 2. Server Prerequisites Installation

Run the following commands on your host instance terminal to install critical system libraries:

```bash
# Update package repositories
sudo apt update && sudo apt upgrade -y

# Install Docker core
sudo apt install -y apt-transport-https ca-certificates curl software-properties-common
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu focal stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Verify installations
docker --version
docker-compose --version
```

---

## 3. Environment Variables Core Setup

Create a root `.env` file containing security credentials:

```bash
# General Engine Settings
PROJECT_NAME="ClipForge AI"
GEMINI_API_KEY="AI_STUDIO_INJECTED_KEY_HERE"

# Database Configurations
DATABASE_URL="postgresql://clipforge_user:secure_pass@postgres:5432/clipforge_db?schema=public"

# Redis queue configurations
REDIS_URL="redis://redis:6379/0"

# Storage Settings
STORAGE_BUCKET_NAME="clipforge-exports"
AWS_ACCESS_KEY_ID="your_aws_access_key"
AWS_SECRET_ACCESS_KEY="your_aws_secret"
```

---

## 4. Database Setup and Migration

To configure Prisma ORM schema patterns natively:

```bash
# Step inside your web client directory
cd web

# Install dependency configurations
npm install

# Push database schema modeling directly to PostgreSQL
npx prisma db push
```

---

## 5. Launch Docker Environments

Compile and boot all microservices in disconnected detached mode:

```bash
# Navigate to the compose settings folders
cd docker

# Pull and start PostgreSQL, Redis, FastAPI backend API, Celery Workers, and NextJS
docker-compose up --build -d

# Inspect health and logs to verify status checks
docker-compose ps
docker-compose logs -f backend
```

---

## 6. Access Handlers

Once compiled successfully, the workspace exposures will be live at:
* **Web Dashboard Surface:** `http://<your-server-ip>:3000`
* **Swagger API Documentation:** `http://<your-server-ip>:8000/docs`
* **Redis Queue metrics:** `http://<your-server-ip>:6379`
