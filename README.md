# Maintenance

A modern, lightweight maintenance mode plugin for Paper servers. Toggle maintenance instantly, or schedule it in advance — with full bilingual support, hex-color messages, and a polished server list integration.

## Features

- **Instant or scheduled maintenance** — `/maintenance on` for an indefinite maintenance window, or `/maintenance on 30m <reason>` to auto-disable after a set duration, complete with countdown broadcasts.
- **Survives restarts** — a scheduled maintenance window resumes correctly even if the server restarts while it's active.
- **Separate permissions** — `maintenance.admin` for managing the plugin, `maintenance.bypass` for letting staff join during maintenance without granting full command access.
- **Full localization** — messages automatically match each player's client language (English and Japanese included out of the box), with graceful fallback.
- **Hex color support** — every message supports both legacy `&` color codes and modern `&#RRGGBB` hex colors.
- **Server list integration** — a customizable MOTD for normal and maintenance states, with the player count hidden and a maintenance banner shown in its place while maintenance is active.
- **Live reload** — `/maintenance reload` reloads the configuration and language files without a restart.
- **Self-updating config** — new configuration options are automatically added when you update the plugin, without overwriting your existing settings.
- **Optional Modrinth update checker** — notifies operators on join when a new version is available.
- **Tab completion** for every subcommand and common durations.

## Commands

| Command | Description |
|---|---|
| `/maintenance on [duration] [reason]` | Enable maintenance mode. `duration` accepts a number followed by `s`, `m`, `h`, or `d` (e.g. `30m`, `2h`). Both `duration` and `reason` are optional. |
| `/maintenance off` | Disable maintenance mode. |
| `/maintenance status` | Show whether maintenance mode is active, its reason, and any remaining time. |
| `/maintenance reload` | Reload `config.yml` and the language files. |
| `/maintenance help` | Show the command list. |

## Permissions

| Permission | Default | Description |
|---|---|---|
| `maintenance.admin` | `op` | Allows use of the `/maintenance` command. Implies `maintenance.bypass`. |
| `maintenance.bypass` | `op` | Allows joining the server while maintenance mode is enabled. |

## Configuration

`config.yml`:

```yaml
maintenance:
  enabled: false
  reason: ""
  scheduled-end: 0

default-language: "en_us"

motd:
  normal:
    line1: "&aExample Server"
    line2: "&7Survival &8| &f%online%&7/&f%max% &7online"
  maintenance:
    line1: "&aExample Server"
    line2: "&cUnder maintenance - back soon!"
  hide-player-count: true
  version-text: "&c⚠ &4Maintenance"

broadcast:
  on-enable: true
  on-disable: true
  countdown-warnings: true

update-checker:
  enabled: true
  modrinth-id: "your-project-slug"
  notify-ops-on-join: true
```

`update-checker.modrinth-id` should be set to your plugin's actual Modrinth project slug (the part of the URL after `/plugin/`) before enabling the checker.

Language files live in `plugins/Maintenance/lang/` (`en_us.yml`, `ja_jp.yml`) and can be freely edited or extended with additional locales — just add a new `<locale>.yml` file using the same keys.

## Installation

1. Download `Maintenance-<version>.jar` and place it in your server's `plugins` folder.
2. Restart the server.
3. Edit `plugins/Maintenance/config.yml` to taste, then run `/maintenance reload`.

## Requirements

- Paper (or a Paper fork) for Minecraft 1.21.x
- Java 21+

## Building from source

```
mvn clean package
```

The compiled jar will be at `target/Maintenance-<version>.jar`.
