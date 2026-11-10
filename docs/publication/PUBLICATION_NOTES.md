# Publication preparation — 3.6

These are editorial drafts, not a record of publication. No OpenNTF fields or GitHub releases have been changed.

## OpenNTF fields

| Field | Proposed value |
| --- | --- |
| Display name, if renaming is available | XLogs — formerly XPages Log File Reader |
| Summary | OPENNTF_SUMMARY.md |
| Release name | XLogs 3.6 |
| Version | 3.6 |
| New features / release description | RELEASE_3.6.md |
| Source Control | https://github.com/john-dalsgaard/XLogs |
| Defects / issue tracker | https://github.com/john-dalsgaard/XLogs/issues |
| Documentation | https://github.com/john-dalsgaard/XLogs/blob/master/docs/INSTALLATION.md |

Keep the existing OpenNTF project/history and the original demo link. If the display name changes, verify that the old project URL still resolves. Add current screenshots showing the reader and About/debug panel. Keep the original repository as a historical reference in the documentation.

## Download package

Prepared local asset: `dist/XLogs-3.6.zip`, containing the supplied `xlogs.ntf` template, a short README.txt with deployment instructions, RELEASE_NOTES.md, LICENSE.txt, NOTICE.txt, both MIT license files and the docs directory. Do not use GitHub's automatic Source code ZIP as the installation download.

Use a clean release template with the 3.6 design and intended ACL defaults, retaining required help/design content but excluding personal deployment data and saved debug settings. Package a closed, consistent template copy. Debug must be off by default. The recipient signs the design for their environment.

The database binary is a release asset; keep the editable on-disk project in Git. Upload the same ZIP to OpenNTF and to a GitHub release tagged `v3.6`, pointing at the exact source commit used to build it. Include the ZIP's SHA-256 in the release description or as a checksum file.

The supplied working template is `xlogs.ntf` in the project root. Root-level NSF/NTF files and generated `dist/` packages are ignored by Git. The binary has been packaged unchanged; its internal design, ACL and documents have not been inspected by the packaging script. Before publishing, confirm the template contents. The maintainer has confirmed testing and operation on Domino 9.0.1 FP10 and selected versions through Domino 14.5.1 FP1; this statement is included in the README, installation guide, summary and release description. It does not claim testing of every intermediate release.

The existing third-party attribution follow-ups in docs/THIRD_PARTY.md remain relevant to publication. This drafting step does not resolve them.
