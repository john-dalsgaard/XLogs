# XLogs — formerly XPages Log File Reader

XLogs is a browser-based log viewer and troubleshooting tool for HCL Domino and XPages. It brings server logs, configuration files and environment information together in one application, helping developers and administrators investigate problems through their web browser.

## What can it do?

- Read console and XPages logs, log.nsf, OSGi startup logs, and XPages XML error and trace logs.
- Inspect design update logs, HTTP request logs and other configured text files.
- View Traveler error, activity, usage, audit and system dump logs when available, and download archived log files.
- Browse files, download them, highlight important messages and hide selected categories using configurable filters.
- Navigate text logs with selectable page sizes from 250 to 50,000 lines. Filter choices are retained during navigation.
- Inspect server configuration files such as notes.ini, server-wide xsp.properties, java.policy, java.pol and rcpinstall.properties, when present and accessible.
- Inspect server, Java, XPages and user information, and browse extension JAR files.

Menus, file paths, encodings and filters can be customized in config.xml. Access depends on the database ACL, server permissions and the files available on the server.

## Project continuation

XLogs continues **XPages Log File Reader**, originally developed by **Jakob Majkilde**. After extending the application for his own use over several years, **John Dalsgaard** has taken over maintenance with Jakob's agreement. Version 3.6 makes this continued development available to the community under the XLogs name.

The project remains open source under the **Apache License 2.0**, with the original credits and third-party notices retained.

## Getting started

Download and extract the packaged template, create a database on your Domino server from xlogs.ntf, sign the application design with an appropriate ID and configure the database ACL before use. See the installation guide for details.

- [Source code](https://github.com/john-dalsgaard/XLogs)
- [Installation and configuration](https://github.com/john-dalsgaard/XLogs/blob/master/docs/INSTALLATION.md)
- [Configuration reference](https://github.com/john-dalsgaard/XLogs/blob/master/docs/CONFIGURATION.md)
- [Bug reports and feature requests](https://github.com/john-dalsgaard/XLogs/issues)
- [GitHub releases](https://github.com/john-dalsgaard/XLogs/releases)

[Watch the original demo](https://www.youtube.com/watch?v=2eE6NIYUzN4). This video demonstrates an earlier version; the current interface and features have evolved.
