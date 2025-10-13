# ae-sticker-app Project Instructions

This document provides guidance for the AI assistant working on the `ae-sticker-app` project.

## Project Overview

- **Project Name:** ae-sticker-app
- **Package Name:** com.ae.apps.stickerapp
- **Description:** This is a sticker pack application for WhatsApp. The main functionality is to allow users to browse and install sticker packs.

## Key Components

- **Sticker Packs:** Defined in `app/src/main/assets/contents.json`. This file contains the metadata for each sticker pack.
- **UI:** The UI consists of a list of sticker packs. The layout for each item is in `app/src/main/res/layout/sticker_pack_list_item_image.xml`.
- **Activities:**
    - `EntryActivity`: The main entry point of the application.
    - `BaseActivity`: A base activity with common functionality.
- **Features:**
    - **Advertisements:** The app displays ads. See `AdLoadedCallback.java`.
    - **In-App Reviews:** The app prompts users for reviews. See `AppReview.java`.

## Development Guidelines

- When adding a new sticker pack, make sure to update `contents.json` with the correct information.
- Follow the existing coding style and conventions.
- Make sure to test the app on different Android versions.
