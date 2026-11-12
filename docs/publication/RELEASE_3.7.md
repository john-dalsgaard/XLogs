# XLogs 3.7

## New features and improvements

- **Tika logs:** read and download `ndtika*` files under Logs, with WARN and ERROR highlighting. Tika appears after OSGi startup log.
- **Diagnostics tab:** browse NSD logs, NSD trace output, semaphore logs, Java dumps, console logs, system information, configuration DXL snapshots and additional support logs. Diagnostics appears after Ext.
- **Combined support download:** All support files brings these diagnostic categories together so files from several categories can be downloaded in one ZIP.
- **Select multiple files:** compact checkboxes next to each file and a “Download N selected as ZIP” link in the Current file heading. ZIP downloads contain the complete original files, independent of display limits and filters.
- **Predictable navigation:** selections persist across file-list pages within a category. Changing categories clears selection and returns the file list to its first page.
- **Bounded-memory ZIP transfer:** archives are built in a temporary server file, streamed to the browser and removed afterwards.

## Installation and upgrade

Extract the ZIP and use Notes/Administrator to create a database on the Domino server from `xlogs.ntf`. Sign the application design with an appropriate ID and review the database ACL before opening `Reader.xsp`.

Before upgrading, back up the database and preserve the ACL and any customized `config.xml` or styles. Refresh or replace the design from the template, re-sign as required, and verify log reading, access, category navigation and ZIP downloads. Reapply reviewed configuration customizations while retaining the new Tika and Diagnostics entries. Individual file downloads remain available.

## Compatibility

The project has previously been tested on Domino 9.0.1 FP10 and selected releases through Domino 14.5.1 FP1, on Windows and Linux. This is the project's existing compatibility baseline, not a claim that every feature in 3.7 has been retested on every release.

## Documentation and source

- [Installation](https://github.com/john-dalsgaard/XLogs/blob/master/docs/INSTALLATION.md)
- [Configuration](https://github.com/john-dalsgaard/XLogs/blob/master/docs/CONFIGURATION.md)
- [Source](https://github.com/john-dalsgaard/XLogs)
- [Issues and feature requests](https://github.com/john-dalsgaard/XLogs/issues)

XLogs continues Jakob Majkilde's XPages Log File Reader, maintained by John Dalsgaard with Jakob's agreement. Apache License 2.0; third-party components retain their licenses and notices.
