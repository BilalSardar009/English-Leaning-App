# Muntaha's English Learning App

An Android app (Java) for learning English, built for Urdu speakers. Version 7.

## What is in the app

**Content library — 2,257 learnable items**

| Bank | Count | Levels |
|---|---|---|
| Words | 1,375 | Beginner 341 · Intermediate 344 · Advanced 317 · Expert 373 |
| Sentences | 599 | across 10 places, all four levels |
| Idioms | 148 | Intermediate → Expert |
| Phrasal verbs | 135 | Beginner → Expert |

**Four levels.** Beginner, Intermediate, Advanced and Expert. The Expert tier is
genuinely hard vocabulary — *perspicacious*, *obsequious*, *ineffable*,
*quintessential* — so the app does not run out after a week.

## Six things, not twelve

The home screen has six tiles. Everything else is reached from inside one of
them, so there is one obvious place to go for each kind of practice.

| Tile | What is inside |
|---|---|
| **Lessons** | Your level in units of ten words. Flashcards live here too. |
| **Practice** | Five quiz rounds and five games, in one list. |
| **Sentences** | Sentences grouped by place — home, classroom, market and more. |
| **Tenses** | All twelve, taught then practised. |
| **Dictionary** | Words, idioms, phrasal verbs and your saved words, in four tabs. |
| **Translator** | Type any sentence and get a natural translation. |

The header carries your streak, XP and rank — tap it for rewards. The word of
the day sits on the home screen and is learned with one tap. Settings is the
cog in the corner.

### Sentences by place

This is the part built for real conversation. Pick where you are, and practise
what people actually say there:

🏠 At Home · 🎓 Classroom · 🛒 Market · 🩺 Doctor · 🚌 Travel · 🍽️ Restaurant ·
🧒 Friends · 📱 Phone · 💼 Work · 💬 Everyday

Every sentence gives the Urdu, then **two English versions**: a simple one that
gets the meaning across, and an advanced one showing how a fluent speaker would
put it. Both can be spoken aloud.

> اس نے حالات کے سامنے ہتھیار ڈال دیے۔
> **Simple:** He gave up because of the situation.
> **Advanced:** He capitulated to circumstances rather than confront them.

### Practice

Five quiz rounds — word meanings, English and Urdu both ways, fill in the blank,
idioms and phrasal verbs, and a mixed round — all generated fresh each time, so
they never repeat. Then five games:

- **Word Match** — pair each word with its meaning; matched pairs fade away.
- **Spelling Bee** — hear the word, then spell it one letter at a time on letter tiles.
- **Sentence Scramble** — tap shuffled word chips back into order.
- **Speed Round** — 60 seconds with a rising combo multiplier.
- **Odd One Out** — spot the word from a different level.

### Motivation

Daily streaks, XP with a rank every 100 XP, 19 achievements, a daily reminder
notification with today's word, and confetti when something goes right.

### Splash screen

The app opens with the name bouncing letter by letter over a gradient. It is
drawn in code — no GIF, no image file, nothing to download — and tapping skips
straight through.

## The translator

**With a free AI key (recommended).** Paste a free
[Google AI Studio](https://aistudio.google.com/app/apikey) key into
Settings → *AI Translator*. Translations then read naturally rather than word by
word — idioms, slang and blunt phrasing come out as a native speaker would say
them — and each comes with a real explanation of the meaning, any figurative
phrase, and the grammar. The key is stored only in this app's private
preferences on the phone and is sent nowhere except Google's API.

**Without a key.** It falls back to Google ML Kit's on-device model, which works
offline once the language pack downloads but is literal, so idioms come out
oddly. The app says so on screen rather than pretending otherwise.

Either way the tense detector labels the tense of the English side.

## Urdu

Urdu is **off by default**. Turn on Settings → *Show Urdu everywhere* to see Urdu
alongside every word, tense and explanation. The app's own labels stay in English
so the interface never switches language.

## Building

- Android Studio Hedgehog (2023.1.1) or newer
- **JDK 17** — required by Android Gradle Plugin 8.2.2
- compileSdk / targetSdk 34, minSdk 26 (Android 8.0)

Open the project folder (the one containing `settings.gradle`) in Android
Studio, let Gradle sync, then Run.

### Troubleshooting

**`class file has wrong version 61.0, should be 55.0`** — Android Studio is on
JDK 11. AGP 8.2.2 needs 17: *Settings → Build, Execution, Deployment → Build
Tools → Gradle → Gradle JDK*.

**`attribute auto:xxx not found`** — a layout declaring
`xmlns:app="http://schemas.android.com/apk/res/auto"`. The correct value uses a
hyphen: `res-auto`. Every layout here already uses the hyphen form.

**Nothing is spoken** — the device needs a text-to-speech engine with English
installed (*Settings → Accessibility → Text-to-speech output*).

## A note on the name

The app is *Muntaha's App* on the launcher and throughout. The Java package is
still `com.saim.englishlearning`, deliberately: the package doubles as the
`applicationId`, and changing it makes Android treat the result as a different
app, so an installed copy would not update — it would install alongside the old
one with empty progress. Ask and I will migrate it properly.

## Project layout

```
app/src/main/
├── assets/           words.tsv, sentences.tsv, idioms.tsv, phrasals.tsv
├── java/com/saim/englishlearning/
│   ├── model/        Word, Phrase, SentencePair, Tense, QuizQuestion, Achievement
│   ├── data/         WordBank, PhraseBank, SentenceBank, TenseBank,
│   │                 ExerciseGenerator, ProgressManager
│   ├── util/         Anim, ConfettiView, RingProgressView, FlowLayout,
│   │                 BouncingTextView, Speaker, TenseDetector,
│   │                 GeminiClient, OfflineTranslator
│   ├── adapter/      Word, Unit, Phrase, Tense, Achievement adapters
│   ├── game/         The five game screens
│   ├── notifications/ ReminderScheduler, ReminderReceiver, BootReceiver
│   └── *Activity.java
└── res/              28 layouts, 37 drawables, 227 strings
```

### Adding more content

The banks are plain pipe-separated assets — edit and rebuild, no code changes.

- `words.tsv` — `level|word|urdu|meaning|example`
- `sentences.tsv` — `place|level|urdu|simple English|advanced English`
- `idioms.tsv` / `phrasals.tsv` — `level|phrase|meaning|urdu|example`

Levels are `1` Beginner, `2` Intermediate, `3` Advanced, `4` Expert. Valid
places: `everyday`, `home`, `classroom`, `market`, `doctor`, `travel`, `food`,
`friends`, `phone`, `work`. To add a new place, add its rows here and one line
to the `TOPICS` list in `SentenceBank.java`.
