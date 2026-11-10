# Installation and development

## Deploying a packaged database

1. Obtain the NSF/NTF from the release you intend to deploy and read its release notes. If no package is available, build from source as described below.
2. Back up an existing installation before upgrading, including its ACL and any customized design elements such as `config.xml`.
3. Place the application database on the Domino server, or create a database from the supplied template.
4. Sign the design with an ID authorized by your Domino administration policy to execute the application's server-side code and read the required files.
5. Review the database ACL. Grant access only to the intended administrators and troubleshooting users. Review Default and Anonymous access explicitly: the application can display server logs and configuration contents.
6. Open `https://<server>/<database-path>/Reader.xsp` and verify access using an intended user account. The About page is `About.xsp`.

For an upgrade, use the release's supported design replacement/refresh procedure, preserve the existing ACL and reapply reviewed customizations. Verify the database version and representative log entries afterwards.

## Access and configuration

Restricted menu entries check the effective user's server access using Domino's unrestricted-program or view-only-administrator access checks. Menu visibility is not a substitute for a carefully configured database ACL. Actual file access also depends on the server process and application execution permissions.

The file resource `nsf/Resources/Files/config.xml` defines menu items, log paths, filters and read limits. The current defaults limit text reads to 10,000 lines and log database reads to 5,000 documents. Traveler paths use the `NTS_LOG_ROOT_DIR` notes.ini setting. Do not enable additional logging solely to populate a menu without considering the operational impact.

## Working with the source

The `nsf/` directory is an on-disk project containing XPages, custom controls, Java, script libraries, resources and Domino design metadata. Development requires HCL Domino Designer and a Domino server with XPages support. This repository does not include a standalone command-line build or automated Domino runtime tests.

Use Domino Designer's source-control/on-disk-project workflow to associate this directory with a development NSF, synchronize the design and build it. Preserve the supplied `.metadata` files. Check the resulting database for compilation errors, then sign and deploy it to a development server before packaging a release.

Verify log reading, paging, filters, error/trace rendering, access restrictions, About information and optional Traveler entries on each server version claimed by the release.

## Release packaging

Distribute `LICENSE.txt`, `NOTICE.txt` and the third-party license files alongside the NSF/NTF. Preserve the licenses and notices embedded in bundled JARs and source resources. Include exact tested Domino versions, installation/upgrade notes and changes in the release description.

The [original project's documentation](https://github.com/majkilde/LogFileReader/blob/master/doc/Documentation.pdf) is historical reference material. It has not been validated as an installation guide for the current XLogs version.

## Debug logging

In the **web interface**, open **About → Debug logging** with the **[Admin]** ACL role and at least **Editor** web access. Use the existing `Admin` role in the database ACL and assign it to the intended administrator or group. The maximum Internet access can remain Editor. This section is not part of the legacy Notes client interface. Access checks use the effective user's role and web access level; the write action repeats the check. If a user has an explicit ACL entry, assign the role there: Domino's queryAccessRoles checks an explicit entry before group memberships.

Choose **15 minutes**, **30 minutes**, **1 hour**, **3 hours** (the default), or **Until disabled manually**, then click **Enable debug logging**. While logging is active, **Change duration** starts a new period from now. The status and expiry are shown on the page. **End debug logging**, visible only while active, stops either mode. Normal error logging remains enabled independently. Diagnostics and timing output go to the Domino server console/log.

Settings are ordinary background documents with form `XLogsDebugSettings`, one per executing server's full Domino name. A hidden `(XLogsDebugSettings)` view indexes `DebugServer` as its first sorted column. The view is supplied by the database design and must select `Form = "XLogsDebugSettings"`. The application never creates or modifies views. No configuration form, helper document or Notes UI setup is required. Older settings are reused via this view but remain off until saved with an explicit duration.

The application caches only the configuration document's UNID per server. The first lookup uses the keyed view; later requests read directly by UNID. Deleted/replaced documents trigger a new keyed lookup. The configuration values are read once per request, with no database scan and no shared cache of enabled/disabled state. Changes apply on subsequent requests without an HTTP restart. Expiry is checked for each debug call, including within a request. No scheduled cleanup agent is required.

Replicating the database to a different server does not enable debug there because its server key differs. Disabling or expiry does not delete the configuration document.

To verify deployment, sign/synchronize the Java and About design changes, then open `About.xsp` in a browser. Test with a user who receives [Admin] through a group, Editor web access, and a user without the role. Enable timed debug and check `XLogs timing` output while reading a text log. Confirm manual disable, automatic expiry and unlimited mode. With a replica on another server, confirm debug stays off there. The test must use the web login whose effective permissions are being checked.

## Displayed dates

File modification times, the About version date, debug expiry and Ext file dates use `yyyy-MM-dd HH:mm:ss` followed by the server JVM time zone (for example, `2026-11-10 14:35:08 CET`). The format is independent of browser language and regional settings. The time-zone suffix reflects daylight saving time at the displayed timestamp. Dates inside log content retain their original format.
