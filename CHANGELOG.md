# Changelog

## 3.7 — 2026-11-12

- Keep ZIP checkboxes inline with file rows, show the selected count in a conditional download link in the Current file heading, and clear selection when changing categories. Move Tika after OSGi startup log and Diagnostics after Ext.

- Reset file-list paging to the first page when changing categories.
- Add Tika logs under Logs and a Diagnostics tab with combined and categorized support files.
- Add selection across file-list pages and ZIP download of complete original text files, streamed through a temporary archive.

This summary is based on the local commit history. Version headings describe development history, not published GitHub releases. The local history begins with a v3.4 source import; earlier history is in the original repository.

## 3.6 — current development

- Format application timestamps consistently as yyyy-MM-dd HH:mm:ss with the server JVM time zone; preserve original log content.

- Persist the selected debug duration per server so About restores it after navigation, without changing the existing expiry.

- Group debug controls in a responsive About side panel with a prominent active status, and show action messages only once.

- Add server-specific debug controls on web About for users with the [Admin] role and at least Editor web access, using a hidden keyed view and cached UNIDs instead of database searches.
- Offer timed debug (15 minutes to 3 hours) or unlimited debug, with automatic expiry checks and current-session ACL access checks.

- Disable development timing and XML diagnostics through the existing debug switch; retain error logging without duplicate XML stack traces.

- Restore saved filters when paging the file list using a full refresh, and after partial refreshes from content paging and page-size changes.

- Added a Configuration entry for the server-wide `properties/xsp.properties` file when present.

- Added design update log support for Domino 14.x and optional Traveler logs.
- Updated the layout, theme and application icon for XLogs.
- Expanded About information and maintainer details.
- Adjusted Java/JAR lookups for Domino 14.5.
- Improved paging with cached content and error/trace filters.

## 3.5

- Added OSGi startup log access.

## 3.4

- Initial source import into this local repository.
