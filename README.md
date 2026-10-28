# XLogs

A browser-based log viewer and troubleshooting tool for HCL Domino, built with XPages.

XLogs continues **XPages Log File Reader**, originally developed by **Jakob Majkilde** and published on OpenNTF. **John Dalsgaard** maintains this continuation with Jakob's permission. The application has been renamed and developed further; the original authorship and license notices are retained.

- [Current source and issues](https://github.com/john-dalsgaard/XLogs)
- [Releases](https://github.com/john-dalsgaard/XLogs/releases)
- [OpenNTF project](https://www.openntf.org/main.nsf/project.xsp?r=project/XPages%20Log%20File%20Reader)
- [Original repository](https://github.com/majkilde/LogFileReader)

## Features

- Read XPages and console logs, log.nsf, OSGi startup logs, and XPages XML error and trace logs.
- Inspect design update and HTTP request logs when available.
- Read Traveler logs when `NTS_LOG_ROOT_DIR` is configured; the Traveler menu is hidden when no matching entries are available.
- Inspect configuration files such as notes.ini, xsp.properties and Java policy files.
- Browse server, Java, XPages, browser and user information.
- Filter and highlight log content, with paging and cached content.

Available entries depend on the server configuration, files present and user access. Menu entries, file patterns, filters and read limits are defined in [config.xml](nsf/Resources/Files/config.xml).

## Installation and upgrade

See [installation and development](docs/INSTALLATION.md) for deployment, access control and working with the source.

This repository contains a Domino Designer on-disk project under `nsf/`. A GitHub source ZIP is not an installable NSF or NTF. Use a packaged database from a release when one is available, or build from source with Domino Designer.

Recent development includes adjustments for Domino 14.x and Java/JAR locations used by Domino 14.5. A tested server and browser compatibility matrix has not yet been published; these changes do not establish a minimum supported version or guarantee compatibility.

## History

The local Git history starts with a v3.4 source import. Earlier development history remains in Jakob's repository. Subsequent work includes OSGi logs, cached paging, error/trace filters, updated Java paths, Traveler logs and the XLogs layout and icon. See [CHANGELOG.md](CHANGELOG.md).

## Reporting issues

Use [GitHub Issues](https://github.com/john-dalsgaard/XLogs/issues). Include the XLogs version, Domino version, operating system, browser and steps to reproduce. Remove credentials, personal data and internal server details from logs before attaching them.

## License and credits

The project continues under the [Apache License 2.0](LICENSE.txt). Original copyright and attribution notices are retained in [NOTICE.txt](NOTICE.txt). Third-party components have their own licenses; see [third-party notices](docs/THIRD_PARTY.md), the [jQuery license](JQUERY%20MIT%20LICENSE.txt) and the [TableSorter license](TABLESORTER%20MIT%20LICENSE.txt).

Original author: Jakob Majkilde. Current maintainer: John Dalsgaard. The original OpenNTF project and its contributors are gratefully acknowledged.
