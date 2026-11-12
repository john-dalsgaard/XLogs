# Publication preparation — 3.7

Prepared locally; no GitHub or OpenNTF publication has been performed.

## Assets and text

- Installation download for both services: `dist/XLogs-3.7.zip`.
- Checksum: `dist/XLogs-3.7.zip.sha256`.
- Release title: `XLogs 3.7`; version: `3.7`; Git tag: `v3.7`.
- Release description: `docs/publication/RELEASE_3.7.md`.
- Optional updated project summary: `docs/publication/OPENNTF_SUMMARY.md`.
- Packaging provenance and template hash: `dist/PACKAGE_INFO.txt`.

The ZIP contains the supplied `dist/xlogs.ntf`, README.txt, RELEASE_NOTES.md, the license and attribution files, and documentation. The NTF is included unchanged. Its internal design, ACL and documents have not been independently inspected. It is supplied by the maintainer as the release template. GitHub's automatic source archives are not the installation package. Keep the binary in release assets; dist is ignored by Git.

## GitHub

1. Review and commit the intended source and documentation changes, then push that commit to GitHub. Keep the release tag on the source used to build the supplied NTF.
2. Open https://github.com/john-dalsgaard/XLogs/releases/new.
3. Choose/create tag `v3.7` at that source commit and use title `XLogs 3.7`.
4. Paste RELEASE_3.7.md into the release description.
5. Attach XLogs-3.7.zip and XLogs-3.7.zip.sha256.
6. Review the draft, then publish the release. Download the published ZIP and compare its SHA-256 with the local checksum.

GitHub instructions: https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository

## OpenNTF

1. Sign in with the project-owner/committer account and open the existing XPages Log File Reader / XLogs project. Preserve its project history.
2. Use the project's release-creation action. Exact labels depend on the authenticated OpenNTF interface.
3. Set release name `XLogs 3.7` and version `3.7`, and copy the contents of RELEASE_3.7.md into the release description. Adapt Markdown to the editor if needed.
4. Attach the same XLogs-3.7.zip used on GitHub; add the checksum file if the form supports it, otherwise include its hash in the description.
5. Optionally update the project summary from OPENNTF_SUMMARY.md. Keep source and issue links pointing to https://github.com/john-dalsgaard/XLogs and its /issues page.
6. Save/publish, then verify the release download and version. The authenticated release form has not been inspected for this preparation.

Project: https://www.openntf.org/main.nsf/project.xsp?r=project/XPages%20Log%20File%20Reader
