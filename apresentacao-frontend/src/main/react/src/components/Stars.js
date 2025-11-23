import React, { useEffect, useState } from 'react';

const Stars = () => {
  const [twinkles, setTwinkles] = useState([]);
  const [falling, setFalling] = useState([]);

  useEffect(() => {
    const generate = () => {
      const t = [];
      // small static/twinkling stars
      for (let i = 0; i < 180; i++) {
        t.push({
          id: `t-${i}`,
          left: `${Math.random() * 100}%`,
          top: `${Math.random() * 100}%`,
          animationDelay: `${Math.random() * 6}s`,
          size: Math.random() > 0.85 ? '3px' : Math.random() > 0.6 ? '2px' : '1px',
        });
      }

      const f = [];
      // falling/shooting stars
      for (let i = 0; i < 18; i++) {
        f.push({
          id: `f-${i}`,
          left: `${Math.random() * 100}%`,
          // start above viewport
          top: `-10%`,
          // make different speeds and delays
          animationDelay: `${Math.random() * 12}s`,
          duration: `${2 + Math.random() * 4}s`,
          size: Math.random() > 0.8 ? '3px' : '2px',
          angle: `${-20 + Math.random() * 40}deg`,
        });
      }

      setTwinkles(t);
      setFalling(f);
    };

    generate();
  }, []);

  return (
    <div className="stars" aria-hidden>
      {twinkles.map((s) => (
        <div
          key={s.id}
          className="star twinkle"
          style={{
            left: s.left,
            top: s.top,
            width: s.size,
            height: s.size,
            animationDelay: s.animationDelay,
          }}
        />
      ))}

      {falling.map((s) => (
        <div
          key={s.id}
          className="star shooting"
          style={{
            left: s.left,
            top: s.top,
            width: s.size,
            height: s.size,
            animationDelay: s.animationDelay,
            animationDuration: s.duration,
            transform: `rotate(${s.angle})`,
          }}
        />
      ))}
    </div>
  );
};

export default Stars;
