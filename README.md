# Stroke detection system for racket sports 🏸

This project offers a mobile application that connects to a measuring device for the collection of strokes during a training session or match. The mobile application consists broadly of an event management section, similar to an agenda where the user can write down their upcoming matches, training sessions or other events and the application will notify them when it is time to start the event. It also has a real-time display of the strokes the player is making.

The measuring device is based on an Arduino which is able to collect raw data through its built-in IMU unit. This raw data is the acceleration exerted per unit of time and the orientation in space per unit of time. Thanks to an artificial intelligence model previously trained and added to the Arduino's own firmware, we are able to detect each stroke made by the player. The connection between the two devices is made via Bluetooth Low Energy.

## Measuring device

<div style="display: flex; flex-wrap: wrap;">
  <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/Arduino.jpg" alt="Arduino + Battery" width="43%" height="43%">
  <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/Device.jpg" alt="Measuring device" width="40%" height="40%">
</div>

## Android App

<div style="display: flex; flex-wrap: wrap;">
    <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/login.jpg" alt="App: Login" width="30%" height="30%" style="margin: 10px;">
    <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/home.jpg" alt="App: Home" width="30%" height="30%" style="margin: 10px;">
    <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/settings.jpg" alt="App: Settings" width="30%" height="30%" style="margin: 10px;">
</div>


<div style="display: flex; flex-wrap: wrap;">
  <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/events.jpg" alt="App: Events" width="30%" height="30%">
  <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/event.jpg" alt="App: Event" width="30%" height="30%">
  <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/statistics.jpg" alt="App: Statistics" width="30%" height="30%">
</div>

<div style="display: flex; flex-wrap: wrap;">
  <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/ble.jpg" alt="App: BLE Connection" width="30%" height="30%">
  <img src="https://github.com/marioruub/Stroke-detection-system-for-racket-sports/blob/main/img/strokes.jpg" alt="App: Strokes" width="30%" height="30%">
</div>
