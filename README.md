# Locator Lodestones

[Modrinth Page](https://modrinth.com/project/pMBcsVIg)

![The Locator Lodestones mod icon](images/icon_high_res.png)

Adds Waypoints to the Player Locator Bar for each Lodestone Compass and Recovery Compass in your inventory.

The mod works entirely client-side, and doesn't need to be installed on the server.

If the compass has an RGB code in its name (for example, a compass named "Home #00FF8F"),
it will use that color for the waypoint.
Otherwise, the color is randomly determined based on the coordinates of the lodestone.

The names of compasses (minus any RGB codes) can be shown above the bar by holding down the player list key (by default TAB)

The mod also checks bundles, so you can place all of your compasses in a bundle, and it will still work.

![A screenshot of the locator bar pointing towards two lodestones, with one nearby and the other further in the distance](images/screenshot.png)

[Download on Modrinth](https://modrinth.com/project/pMBcsVIg)

# Additional features from this fork

Only consider compasses held by the player within a configurable location:
- whole inventory (default),
- hotbar only,
- hands only.

Optionally adds a compass dial to the locator bar: 
- showing cardinal directions (north ↑, south ↓, west ←, east →)
- showing divisions to estimate angles in between
- displayed when the player has a compass (even if not linked to a lodestone) or a recovery compass
- under a new config option (false by default)

![Compass dial](https://private-user-images.githubusercontent.com/21187022/506148459-8dac59e9-5606-450f-a92f-a20f2edea4b3.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjIyNTEzNzksIm5iZiI6MTc2MjI1MTA3OSwicGF0aCI6Ii8yMTE4NzAyMi81MDYxNDg0NTktOGRhYzU5ZTktNTYwNi00NTBmLWE5MmYtYTIwZjJlZGVhNGIzLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTExMDQlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUxMTA0VDEwMTExOVomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPWIzMDI2NTJlZGJlOTM4MGQ5NTllZWI5NzAzNTg1MTQ3NmY3MTI3ZGVmZGFjZTQxODU1OGMxYmNkN2NjYzQ1ZjAmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.WDPsAXk70UdgVqHED2R9buoxADrDf1NpqQgoyJxEbkc)

Display the distance to the waypoint the player is aiming at:
- only within a +/-10° angle accuracy
- using the waypoint color
- when shown, the experience level is hidden
- working for standard entity waypoints and lodestone waypoints
- under a config option (false by default)

![Distance to waypoint](https://private-user-images.githubusercontent.com/21187022/506247599-3eff8666-6768-414a-bc54-03e145d65053.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjIyNTExNjAsIm5iZiI6MTc2MjI1MDg2MCwicGF0aCI6Ii8yMTE4NzAyMi81MDYyNDc1OTktM2VmZjg2NjYtNjc2OC00MTRhLWJjNTQtMDNlMTQ1ZDY1MDUzLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTExMDQlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUxMTA0VDEwMDc0MFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTY0NGQ2ZDBiODk1ZTJkY2NkMzdmZjBkM2RmYmZkOTY1OGY1NGYyNmY3MTUxZDBjZDA5YmVkYmNiOGFhMWE3NzImWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.yKaurRY2ccNc3j7vzuGda3LxMIeuTFQy7MD2wP3WCa8)

Display spawn point on locator bar:
- when holding a non-lodestone compass
- under config option (default to false)

![Spawn point](https://private-user-images.githubusercontent.com/21187022/506289074-a738330e-c979-4ce3-8232-569f9a8b4de2.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjIyNTExODQsIm5iZiI6MTc2MjI1MDg4NCwicGF0aCI6Ii8yMTE4NzAyMi81MDYyODkwNzQtYTczODMzMGUtYzk3OS00Y2UzLTgyMzItNTY5ZjlhOGI0ZGUyLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTExMDQlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUxMTA0VDEwMDgwNFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTE1YjVjOTdjNmU1YjliNWYxZmZmOTA5ODI3YzFjYjYzMTMwOWM5MDU4MzJmY2I5NWYzMjY3YmRiMzhmMTVjM2QmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.hvorBOgRjZ1aGW2w1TzkuOwvmo0SC9qyy85EoZ7szEQ)

Display map's points of interest as waypoints on the locator bar:
- when holding a filled map
- under config option

![Map's PoI](https://private-user-images.githubusercontent.com/21187022/506791985-87048a8f-4df4-43f2-be89-c7a69bc97ee4.png?jwt=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NjIyNTEyMDQsIm5iZiI6MTc2MjI1MDkwNCwicGF0aCI6Ii8yMTE4NzAyMi81MDY3OTE5ODUtODcwNDhhOGYtNGRmNC00M2YyLWJlODktYzdhNjliYzk3ZWU0LnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTExMDQlMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUxMTA0VDEwMDgyNFomWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTYwNjI1YTk3ZDY4ZjljMjc2YjMyNjE0ZjBiYjI4ZjFiMDIwMzgzYjUwMDkyNzdkOTdlOWYzNGU1ZDU5NDZiMTEmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.K4GrIFemXjDZT2TFulJ8rURZF8pbCZyT5ixZNlEdhis)

Clock held by player chime at night time :
- location configurable (default inventory)
  - inside bundles included (if enabled by config)
- it plays a chime sound (configurable) at the end of every day
- only works in overworld (when the clock itself works)

<hr/>

Configuration file format has been updated as follows (default values shown):
```
{
  "tab_forces_locator_bar": true,
  "tab_shows_names": true,
  "holding_location": "inventory",
  "show_bundled_compasses": true,
  "show_compass_dial": false,
  "show_recovery_compasses": true,
  "show_spawn": false,
  "show_maps": false,
  "show_distance": false,
  "show_in_spectator": false,
  "colors": {
    "lodestone_color": "random",
    "recovery_color": "bce0eb",
    "spawn_color": "6bcf6d",
    "dial_color": "879e7b",
    "color_customization": true
  },
  "clock_location": "inventory",
  "clock_sound": {
    "sound_id": "minecraft:block.note_block.chime"
  }
}
```
