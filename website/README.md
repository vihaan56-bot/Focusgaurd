# FocusGuard Landing & APK Download Page

This directory contains the Vercel-ready landing page for **FocusGuard** Android application downloads.

## Structure

- `index.html`: Modern, responsive mobile-friendly HTML landing page.
- `styles.css`: Custom productivity-themed green/blue CSS styling.
- `vercel.json`: Vercel routing configuration with `application/vnd.android.package-archive` attachment headers.
- `public/FocusGuard-debug.apk`: Downloadable Android Debug APK.

## Deploying to Vercel

### Method 1: Using Vercel CLI (Recommended)

1. Open terminal inside the `website` directory:
   ```bash
   cd f:\FocusGuard\website
   ```
2. Run Vercel CLI:
   ```bash
   npx vercel
   ```
3. Follow the prompts:
   - **Set up and deploy?**: `y`
   - **Which scope?**: Choose your personal/team account
   - **Link to existing project?**: `n`
   - **Project Name**: `focusguard-app`
   - **In which directory is your code located?**: `./`

### Method 2: Importing GitHub Repository to Vercel Dashboard

1. Push your repository to GitHub.
2. Log into [Vercel Dashboard](https://vercel.com).
3. Click **Add New** > **Project** and select your `FocusGuard` repository.
4. Set **Root Directory** to `website`.
5. Click **Deploy**.

Once deployed, users visiting your Vercel URL can click **Download FocusGuard APK** to download `FocusGuard-debug.apk` directly onto their Android devices.
