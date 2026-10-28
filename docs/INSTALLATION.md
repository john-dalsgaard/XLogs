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
