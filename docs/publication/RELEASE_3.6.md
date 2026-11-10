# XLogs 3.6

## New features and improvements

XLogs 3.6 continues Jakob Majkilde's XPages Log File Reader under a new name and maintainer. John Dalsgaard has extended the application for his own use over several years and is now sharing that work with the community, with Jakob's agreement.

- **Updated for newer Domino environments:** adjustments for Domino 14.x design update logs and the Java/JAR locations used by Domino 14.5.
- **More logs:** OSGi startup logs and optional Traveler log categories, including downloads of archived Traveler logs. The Traveler tab is hidden when there are no accessible matching entries.
- **Improved text-log navigation:** snapshot-based paging with 250, 1,000, 5,000, 10,000 or 50,000 lines per page. Navigation controls reflect the available older and newer content.
- **Persistent filters:** selected filters are restored when switching files, paging or changing page size. Filtering and presentation of error and trace content have also been improved.
- **Refreshed interface:** a new XLogs theme and application icon, expanded About information and a dedicated debug administration panel.
- **Debug controls:** users with the [Admin] role and at least Editor web access can enable diagnostics for the current database/server. Choose 15 minutes, 30 minutes, one hour, three hours or manual deactivation. Timed debug stops automatically, and the selected duration is retained.
- **Consistent dates:** application timestamps use yyyy-MM-dd HH:mm:ss with the server JVM time zone. Original log content keeps its own formatting.
- **Configuration access:** server-wide properties/xsp.properties is available when the file exists and the user has the required permissions.
- **Documentation:** current installation and configuration guides, preserved historical documentation, and original license and attribution files.

## Installation and upgrade

Use the packaged xlogs.ntf template to create the application database; GitHub's automatically generated source archives contain the on-disk project and are not installable NSF databases.

For a new installation, extract the ZIP and use Notes/Administrator to create a database on the Domino server from xlogs.ntf. Sign the application design with an appropriate ID and review the ACL before opening Reader.xsp.

Before upgrading, back up the database and preserve the ACL and any customized config.xml or styles. Refresh or replace the design using the supplied template, re-sign as required by your environment and verify access and log reading. Assign the existing [Admin] role to the intended debug administrators; maximum Internet access can remain Editor.

Read the [installation guide](https://github.com/john-dalsgaard/XLogs/blob/master/docs/INSTALLATION.md) and [configuration reference](https://github.com/john-dalsgaard/XLogs/blob/master/docs/CONFIGURATION.md).

## Source and feedback

- Source: https://github.com/john-dalsgaard/XLogs
- Issues and feature requests: https://github.com/john-dalsgaard/XLogs/issues

Released under Apache License 2.0; third-party components retain their respective licenses and notices.
