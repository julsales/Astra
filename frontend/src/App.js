import React, { useState } from 'react';
import './App.css';
import Login from './components/Login';
import Register from './components/Register';
import Stars from './components/Stars';

function App() {
  const [showRegister, setShowRegister] = useState(false);

  return (
    <div className="App">
      <Stars />
      {showRegister ? (
        <Register onBackToLogin={() => setShowRegister(false)} />
      ) : (
        <Login onRegisterClick={() => setShowRegister(true)} />
      )}
    </div>
  );
}

export default App;
