#include <avr/interrupt.h>
#include <LiquidCrystalRus.h>

struct MorseTree {
  char sym;
  int8_t dot;  // dot index 
  int8_t dash; // dsh index
  uint8_t dummy;
};

int pauseCount = 0;
uint8_t treeIndex = 0;
char playedSym = 0;
extern const MorseTree PROGMEM morseTree[];
//extern MorseTree morseTree[];
 
// инициализируем объект-экран, передаём использованные 
// для подключения контакты на Arduino в порядке:
// RS, E, DB4, DB5, DB6, DB7
LiquidCrystalRus lcd(7, 8, 9, 10, 11, 12);

const int SPEED_PIN = A0;
const int MIN_SPEED = 40, MAX_SPEED = 300;

const int DOT_PIN = (1 << 3);  // PC3
const int DASH_PIN = (1 << 4); // PC4
const uint8_t DOT = 1, DASH = 2, 
DOD = 4,   // Dot over Dash
FDASH = 8; // Force Dash

uint8_t speed = 40, toneTicks = 90, keyFlag = 0; 

// toneMode < 0 mute, > 0 tone 
int16_t toneMode = -toneTicks;
uint16_t timeCount = 0;
uint8_t sinCount = 0;

// dump 600 Hz sin
const uint8_t PROGMEM SinData[] = {0,15,55,112,172,222,250,250,222,172,112,55,15};


inline void updateKeyFlag() {
    uint8_t pc = PINC;

    if((pc & DASH_PIN) == 0) keyFlag |= DASH;

    if((pc & DOT_PIN) == 0) {
      if((keyFlag & DASH) != 0) {
        if((keyFlag & DOD) == 0) keyFlag |= DOD;
      } else {
        keyFlag |= DOT;
      }       
    }  
}

inline void resetKeyFlag() {
    uint8_t pc = PINC;
    if((pc & DASH_PIN) != 0) {
      keyFlag &= ~(DASH|FDASH);
    }

    if((pc & DOT_PIN) != 0) {
      keyFlag &= ~DOT;
    }  
}

inline void updatePause() {
  pauseCount++;

  // waits 3 dots & check played symbol
  if(pauseCount >= 3 && treeIndex != 0) {
    if( treeIndex < 0 ) playedSym = '\xFF';
    else {
//      playedSym = morseTree[treeIndex].sym;
      uint32_t *ptr = (uint32_t *)morseTree;
      uint32_t tval = pgm_read_dword(ptr + treeIndex);
      MorseTree *tt = (MorseTree*)&tval;
      playedSym = tt->sym;
    }

    treeIndex = 0;
  }
}

ISR(TIMER2_OVF_vect) {
  if(toneMode > 0) {
    OCR2B = SinData[sinCount];    
  }
  updateKeyFlag();  

//  // on pause check key
//  if(toneMode < 0)
//    resetKeyFlag();

  sinCount++;
  if(sinCount < sizeof(SinData)) return;
    
  // each 0,001664 secs one Syn Circle
  sinCount = 0;
  timeCount++;
  
  if(timeCount < abs(toneMode)) return;

  // each dot time
  timeCount = 0;
  if(toneMode > 0 ) {
    toneMode = -toneTicks; // mute one dot
    OCR2B = 0xFF;
    pauseCount = 0; 
    
    resetKeyFlag();
  } else {
    updatePause();

    int makeFlags = 0;
    if((keyFlag & (DOD | FDASH)) == DOD) {
      toneMode = toneTicks; // play dot
      keyFlag &= ~DOD;
      keyFlag |= FDASH; // puls dot - dash if both pressed 

      // mark play dot
      makeFlags |= DOT;
    } else if((keyFlag & (DASH|FDASH)) != 0) {
      toneMode = 3 * toneTicks; // play dash
      keyFlag &= ~FDASH;
      
      makeFlags |= DASH;
      // mark play dash
    } else if((keyFlag & DOT) != 0) {
      toneMode = toneTicks; // play dot

      makeFlags |= DOT;
    }

    if(makeFlags != 0 && treeIndex >= 0) {
      uint32_t *ptr = (uint32_t *)morseTree;
      uint32_t tval = pgm_read_dword(ptr + treeIndex);
      MorseTree *tt = (MorseTree *)&tval;
      if((makeFlags & DOT) != 0) treeIndex = tt->dot;
      else treeIndex = tt->dash;
    }
  }
//  resetKeyFlag();
}

void setup() {
  MorseTree* cur = morseTree;
    
  // CS2[2:0] = 010;  // prescaller = 8 (Freq = 7812,5 Hz)
  // WGM[2:0] = 011;  // fast PWM
  // COM2B[1:0] = 10; // clear OC0B on compare
  TCCR2A |= ((1 << WGM20) | (1 << WGM21) | (1 << COM2B1));
  TCCR2B |= (1 << CS21); 
  TCCR2B &= ~(_BV(CS20) | _BV(CS22) | _BV(WGM22));
  
  // enable  TOV2 interrupt
  TIMSK2 |= (1 << TOIE2);
  
  // PWM OC2B
  pinMode(PD3, OUTPUT); 
  OCR2B = 0xFF; // mute output
  
  pinMode(SPEED_PIN, INPUT);

  // set DASH & DOT intpu pull-up
  DDRC &= ~(DOT_PIN | DASH_PIN);
  PORTC |= DOT_PIN | DASH_PIN;
    
  // устанавливаем размер (количество столбцов и строк) экрана
  lcd.begin(16, 2);

  lcd.setCursor(0, 0);
  lcd.print("Скорость");

//  lcd.setCursor(0, 1);

  Serial.begin(9600);
  
//  Serial.print("test");
}

void printSpeed(int value) {
  char buf[12];

  // char speed
  itoa(value, buf , 10);
  if(value < 10) {
    buf[1] = ' ';
    buf[2] = ' ';
  } else if(value < 100) {
    buf[2] = ' ';
  }
  buf[3] = '/';

  // digit speed;
  value = value * 7 / 10;
  itoa(value, buf + 4 , 10);
  if(value < 10) {
    buf[5] = ' ';
    buf[6] = ' ';
  } else if(value < 100) {
    buf[6] = ' ';
  }
  buf[7] = 0;
  
  lcd.setCursor(2, 1);
  lcd.print(buf);
  
  //Serial.println(buf);
}
 
#define VALUES_LENGTH 32
uint8_t  valueIdx = 0;
uint16_t values[VALUES_LENGTH], valueSum = 0;

void loop() {

  // make average 
  uint16_t cv = analogRead(SPEED_PIN);
  valueSum += cv;
  valueSum -= values[valueIdx];
  values[valueIdx++] = cv;
  if(valueIdx >= VALUES_LENGTH)
    valueIdx = 0;
  cv = (((valueSum / VALUES_LENGTH) * 3) >> 4) + MIN_SPEED;
  
  if(cv != speed) {
    // 180288,461538462÷(200 × 76 − 30) //
    // 180288,461538462÷(200 × 106 − 30)
    speed = cv;
    toneTicks = (uint8_t)(180288 / ((uint16_t)speed * 76 - 30));
    printSpeed(speed);
  }

  if(playedSym != 0) {
    if(Serial) {
      Serial.print(playedSym);
    } 
    {
      lcd.setCursor(0, 1);
      lcd.print(playedSym);
    }
    playedSym = 0;
  }  
}

const MorseTree PROGMEM morseTree[] = {
  {'\xFF',  1, 25}, //  0
  {'E',  2, 15}, // . 1
  {'I',  3,  9}, // .. 2
  {'S',  4,  7}, // ... 3
  {'H',  5,  6}, // .... 4
  {'5', -1, -1}, // ..... 5
  {'4', -1, -1}, // ....- 6
  {'V', -1,  8}, // ...- 7
  {'3', -1, -1}, // ...-- 8
  {'U', 10, 11}, // ..- 9
  {'F', -1, -1}, // ..-. 10
  {'\xFF', 12, 14}, // ..-- 11
  {'\xFF', 13, -1}, // ..--. 12
  {'?', -1, -1}, // ..--.. 13
  {'2', -1, -1}, // ..--- 14
  {'A', 16, 21}, // .- 15
  {'R', 17, 18}, // .-. 16
  {'L', -1, -1}, // .-.. 17
  {'\xFF', 19, -1}, // .-.- 18
  {'\xFF', -1, 20}, // .-.-. 19
  {'.', -1, -1}, // .-.-.- 20
  {'W', 22, 23}, // .-- 21
  {'P', -1, -1}, // .--. 22
  {'J', -1, 24}, // .--- 23
  {'1', -1, -1}, // .---- 24
  {'T', 26, 36}, // - 25
  {'N', 27, 33}, // -. 26
  {'D', 28, 31}, // -.. 27
  {'B', 29, 30}, // -... 28
  {'6', -1, -1}, // -.... 29
  {'=', -1, -1}, // -...- 30
  {'X', 32, -1}, // -..- 31
  {'/', -1, -1}, // -..-. 32
  {'K', 34, 35}, // -.- 33
  {'C', -1, -1}, // -.-. 34
  {'Y', -1, -1}, // -.-- 35
  {'M', 37, 43}, // -- 36
  {'G', 38, 42}, // --. 37
  {'Z', 39, 40}, // --.. 38
  {'7', -1, -1}, // --... 39
  {'\xFF', -1, 41}, // --..- 40
  {',', -1, -1}, // --..-- 41
  {'Q', -1, -1}, // --.- 42
  {'O', 44, 46}, // --- 43
  {'\xFF', 45, -1}, // ---. 44
  {'8', -1, -1}, // ---.. 45
  {'\xFF', 47, 48}, // ---- 46
  {'9', -1, -1}, // ----. 47
  {'0', -1, -1}, // ----- 48  
};
