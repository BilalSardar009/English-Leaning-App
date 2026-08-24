# Saim's English Learning App

An Android app (Java) for learning English, built for Urdu speakers. Version 6.

## What is in the app

**Content library — 1,959 learnable items**

| Bank | Count | Levels |
|---|---|---|
| Words | 1,375 | Beginner 341 · Intermediate 344 · Advanced 317 · Expert 373 |
| Idioms | 148 | Intermediate → Expert |
| Phrasal verbs | 135 | Beginner → Expert |
| Sentence pairs | 301 | Beginner → Expert |

Every word carries an Urdu translation, a plain-English meaning and an example
sentence. Every sentence carries the Urdu original **plus two English versions**:
a simple one that gets the meaning across, and an advanced one showing how a
fluent speaker would actually say it.

**Four levels.** Beginner, Intermediate, Advanced and Expert. The Expert tier is
genuinely hard vocabulary — *perspicacious*, *obsequious*, *ineffable*,
*quintessential* — so the app does not run out after a week.

### Screens

- **Lessons** — the level split into units of ten words, with per-unit progress.
- **Practice** — seven exercise types, generated fresh every time so they never
  repeat: word meanings, English→Urdu, Urdu→English, fill in the blank, idiom
  meanings, phrasal verbs, and a mixed round.
- **Games** — five arcade-style modes:
  - *Word Match* — pair each word with its meaning; matched pairs fade out.
  - *Spelling Bee* — hear the word, then spell it; hints reveal a letter at a cost.
  - *Sentence Scramble* — tap shuffled word chips back into the right order.
  - *Speed Round* — 60 seconds, rising combo multiplier.
  - *Odd One Out* — spot the word from a different level.
- **Tenses** — all twelve, each with a formula, tappable spoken examples and its
  own practice set with a progress bar.
- **Sentences** — translation practice in both directions, revealing the simple
  and advanced English side by side.
- **Idioms & Phrasal Verbs** — searchable library with meanings and examples.
- **Flashcards** — tap to flip, with text-to-speech.
- **Dictionary** — search the whole bank in English, Urdu or by meaning.
- **Translator** — type any Urdu or English sentence and get a natural
  translation, a real explanation and the tense it uses.
- **Word of the Day**, **Favourites**, **Rewards** (19 achievements, XP, ranks,
  streaks) and **Settings**.

### Motivation

Daily streaks, XP with ranks every 100 XP, 19 achievements, a daily reminder
notification with today's word, and confetti when something goes right.

### Animations

Staggered card entrances, spring pops on tap, shake on a wrong answer, animated
count-up scores, card flips, animated progress rings and a self-drawn confetti
burst — no animation libraries, so nothing extra to download. All of it can be
switched off in Settings.

## The translator

The translator works in two modes.

**With a free AI key (recommended).** Paste a free
[Google AI Studio](https://aistudio.google.com/app/apikey) key into
Settings → *AI Translator*. Translations then read naturally rather than word by
word — idioms, slang and blunt phrasing come out as a native speaker would say
them — and each one comes with a real explanation covering what the sentence
means, any figurative phrase in it, and the grammar. The key is stored only in
this app's private preferences on the phone and is sent nowhere except Google's
API.

**Without a key.** The app falls back to Google ML Kit's on-device model. It
works offline once the language pack downloads, but it is literal, so idioms come
out oddly. The app says so on screen rather than pretending otherwise.

Either way, the tense detector labels the tense of the English side.

## Urdu

Urdu is **off by default**. Turn on Settings → *Show Urdu everywhere* to get Urdu
alongside every word, tense explanation, phrase and AI explanation. All the app's
own labels stay in English so the interface itself does not switch languages.

## Building

- Android Studio Hedgehog (2023.1.1) or newer
- **JDK 17** — required by Android Gradle Plugin 8.2.2
- compileSdk / targetSdk 34, minSdk 26 (Android 8.0)

```
git clone https://github.com/BilalSardar009/English-Leaning-App.git
```

Open the folder in Android Studio, let Gradle sync, then Run.

### Troubleshooting

**`class file has wrong version 61.0, should be 55.0`** — Android Studio is
running on JDK 11. AGP 8.2.2 needs JDK 17. Set
*Settings → Build, Execution, Deployment → Build Tools → Gradle → Gradle JDK* to
17, or update Android Studio.

**`attribute auto:xxx not found`** — this comes from a layout declaring
`xmlns:app="http://schemas.android.com/apk/res/auto"`. The correct value uses a
hyphen: `http://schemas.android.com/apk/res-auto`. Every layout here already
uses the hyphen form.

**Nothing is spoken** — the device needs a text-to-speech engine with English
installed (*Settings → Accessibility → Text-to-speech output*).

## Project layout

```
app/src/main/
├── assets/           words.tsv, sentences.tsv, idioms.tsv, phrasals.tsv
├── java/com/saim/englishlearning/
│   ├── model/        Word, Phrase, SentencePair, Tense, QuizQuestion, Achievement
│   ├── data/         WordBank, PhraseBank, SentenceBank, TenseBank,
│   │                 ExerciseGenerator, ProgressManager
│   ├── util/         Anim, ConfettiView, RingProgressView, FlowLayout,
│   │                 Speaker, TenseDetector, GeminiClient, OfflineTranslator
│   ├── adapter/      Word, Unit, Phrase, Tense, Achievement adapters
│   ├── game/         Games hub + the five game screens
│   ├── notifications/ ReminderScheduler, ReminderReceiver, BootReceiver
│   └── *Activity.java
└── res/              30 layouts, 34 drawables, 249 strings
```

### Adding more content

The banks are plain tab-separated assets — edit them and rebuild, no code
changes needed.

- `words.tsv` — `level|word|urdu|meaning|example`
- `sentences.tsv` — `level|urdu|simple English|advanced English`
- `idioms.tsv` / `phrasals.tsv` — `level|phrase|meaning|urdu|example`

Levels are `1` Beginner, `2` Intermediate, `3` Advanced, `4` Expert.
