# Upstream handover review

Reviewed 2026-11-10 against `majkilde/LogFileReader` master `3275b894da659bb21e528e68a4eade0fa523b463` and develop `adf20bca67f75b191baecb3813e557c2c99d1d74`. This is a source/documentation review, not a claim that historical issues have passed runtime regression tests.

## Material retained

- LICENSE.txt and both jQuery/TableSorter MIT files are byte-for-byte identical to upstream.
- NOTICE.txt retains the complete original file as its prefix, followed by the XLogs continuation attribution.
- The only separate upstream documentation file is the original eight-page PDF. It is preserved unchanged under `docs/legacy/`, with historical status clearly labeled.
- Current configuration instructions are in [CONFIGURATION.md](CONFIGURATION.md), including paths, encodings, filters and access restrictions.
- Earlier commit history remains upstream. This repository's history starts with the v3.4 import; it should not be described as a complete upstream Git history.

The legacy manual explains that `[Admin]` exposed Notes help-authoring views and a Create button for the original maintainer. These are historical Notes features; XLogs configuration does not require authoring help documents. In XLogs, the same role also controls web debug administration.

## Historical issues and pull requests

| Upstream item | Current source assessment / follow-up |
| --- | --- |
| [#6 HTML in logs](https://github.com/majkilde/LogFileReader/issues/6), [PR #7](https://github.com/majkilde/LogFileReader/pull/7) | TextFile already calls escapeHtml4 before filters/rendering. The proposed fix's behavior is present; no blind cherry-pick is needed. Confirm with an HTML/script-containing log fixture before marking externally resolved. |
| [#1 SEVERE/OpenLog messages](https://github.com/majkilde/LogFileReader/issues/1) | OpenLog is commented out in current config.xml. NSFAction already guards db.open() with !db.isOpen(), as suggested in a 2026 comment. Reproduce any remaining case before claiming resolution. |
| [#4 XML trace/error display](https://github.com/majkilde/LogFileReader/issues/4) | A comment reports malformed CommonBaseEvents XML on Linux. XMLFile currently uses a normal transformer and falls back to text on failure; there is no explicit repair of malformed XML. Retain as a compatibility test case. |
| [#5 IBM i/EBCDIC](https://github.com/majkilde/LogFileReader/issues/5) | Encoding is configurable, but the supplied console entry explicitly uses UTF-8. No IBM i detection or validated EBCDIC configuration is present. Needs platform testing. |
| [#2 HTTP 500 / heap](https://github.com/majkilde/LogFileReader/issues/2), [PR #3](https://github.com/majkilde/LogFileReader/pull/3) | Historical discussion points to config/OpenLog differences; the original trace also shows OutOfMemoryError. Current paging reduces rendered HTML, but TextFile still reads the file snapshot into memory. Do not describe large-file heap exhaustion as fully solved. |

The upstream develop branch adds an `XPages.dk` entry for `dk.xpages.*.log`, which is absent from the current XLogs menu. Treat this as an optional configuration example, not an automatic merge: that branch also changes menu IDs, re-enables OpenLog and contains mismatched Error/Warning regex labels. Other relevant changes, such as trace.xsl and Domino API logs, are already represented locally.

The upstream GitHub releases endpoint returned no published releases at review time; this does not describe OpenNTF's historical downloads.

## Remaining attribution work

The inherited NOTICE lists Commons Lang/IO, jQuery, TableSorter, an XML parser and icons. Keep these notices in source and release packages. Exact icon authors/license versions, provenance of newer artwork and the original XML-parser permission still need confirmation; see [THIRD_PARTY.md](THIRD_PARTY.md). No license text was replaced or relicensed during this review.

## Suggested publication follow-up

Prepare a short README redirect in the old repository, pointing users to XLogs for current code, releases and issues. Update OpenNTF's project links and current instructions while preserving its history. Review historical issues on the new project with links to the originals rather than silently closing old reports. No old issues, PRs or repository settings were changed by this review.
