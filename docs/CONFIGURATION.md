# Configuring XLogs

XLogs builds its menu from the `config.xml` file resource under `nsf/Resources/Files/`. Edit it in Domino Designer and synchronize/build the database design. Preserve local customizations before a design upgrade. The following documents current code, drawing on the original Log File Reader manual.

## Menu entries and file readers

The first menu level defines tabs; nested items define the entries within a tab. A text reader can be added inside the existing Logs tab:

```xml
<item name="My application" restricted="true">
  <action type="filereader" filetype="text">
    <filename encoding="UTF-8">[data]/IBM_TECHNICAL_SUPPORT/myapp*.log</filename>
    <filters>
      <filter applyto="line" color="darkred" visible="true" name="Errors">(?i)error</filter>
      <filter applyto="line" background="yellow" visible="true" name="Warnings">(?i)warning</filter>
    </filters>
  </action>
</item>
```

Use `restricted="true"` for administrative files. Restricted entries use the server access checks described in [installation](INSTALLATION.md); the `[Admin]` debug role is a separate permission. Database ACL access still needs review for unrestricted entries.

Supported actions are `link` (a URL), `filereader` and `nsf` (a configured database/view with field/formula presentation). File-reader types are `text`, `xml` and `log`. The `log` reader reads log.nsf EventList content; it is distinct from a general `nsf` action. XML readers use a `<transform>` resource such as `trace.xsl` or `transform.xsl`.

## Paths and encodings

| Prefix | Resolves to |
| --- | --- |
| `[data]` | Domino data directory |
| `[program]` | Domino program directory |
| `[config]` | Directory containing notes.ini |
| `[java]` | JVM `java.home` |
| `[user]` | JVM `user.home` |
| `[travelerlogs]` | `NTS_LOG_ROOT_DIR`, falling back to `IBM_TECHNICAL_SUPPORT/traveler/logs` under the data directory |

Paths can use `/`; the resolver normalizes path separators for the operating system. `*` matches file-name characters within the selected directory; it is not a recursive directory search. Text-reader actions can contain multiple `<filename>` elements.

The optional `encoding` attribute belongs on `<filename>` (the original manual contains an `encodinf` typo). Text readers default to UTF-8. On IBM i, console logs may use EBCDIC; configure the charset matching the actual server file and verify it with a real sample. No automatic IBM i charset detection is implemented.

## Filters

Filters use Java regular expressions. `applyto="line"` styles the entire matching line, with the first matching line filter winning. `applyto="selection"` styles matching portions. Supported styling includes `color`, `background`, `bold`, `visible` and a custom CSS `class`; custom styles belong in `custom.css`.

`visible` controls the initial checkbox state. User choices are kept in browser session storage by database/category/filter. They are reapplied when content is refreshed. Text log lines are HTML-escaped before filtering and rendering.

## Paging and read limits

Text pages offer 250, 1,000 (default), 5,000, 10,000 or 50,000 lines. The legacy `MaxTextLines` configuration remains in config.xml but is not used as a hard text-read limit by the current TextFile implementation. Paging limits rendered lines; the selected file snapshot is still read into memory, so it is not a streaming guarantee for arbitrarily large files.

`MaxLogFileDocs` currently limits log.nsf reads to 5,000 documents. See the actual `<limits>` entries in config.xml when changing deployment defaults.
