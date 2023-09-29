# Aerial Fishing Timers
Add timers to indicate when aerial fishing spots will expire. Aerial fishing spots will stay active anywhere between 10 and 19 ticks.

![java_KaY4wy04se](https://github.com/call-me-maple/aerial-fishing-timers/assets/85463994/6b9d7c00-241c-4a7f-831f-d7908d56faf7)

# Settings
#### Circle Size
Used to change how big the circle indicator will be rendered.
> default: 22

> range: 1-50
#### Warning Threshold
Used to enable and modify a warning color that shows up before the final RNG expiration phase. This feature is meant to help pick out which spot to target next by allowing you to account for your bird's travel time. Use a value of 0 to not have a warning phase.

![java_aKB4O0WtOx](https://github.com/call-me-maple/aerial-fishing-timers/assets/85463994/599bad64-a80d-4608-8881-5cbfc1e061be)
> default: 0

> range: 0-9
### Colors
#### Available Color
Used to change the color of the circle when the spot cannot expire.
> default: Green
#### Warning Color
Used to change the color of the circle right before the RNG expiration phase.
> default: Yellow
#### Expiring Color
Used to change the color of the circle when the spot may expire at any moment.
> default: Orange
