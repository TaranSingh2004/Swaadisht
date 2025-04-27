// Simple Express server to serve the HTML landing page
const express = require('express');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 5001;

// Serve static files from the root directory
app.use(express.static(path.join(__dirname, './')));

// Serve the main HTML file
app.get('*', (req, res) => {
  res.sendFile(path.join(__dirname, 'index.html'));
});

app.listen(PORT, '0.0.0.0', () => {
  console.log(`Swaadisht landing page server running on port ${PORT}`);
});