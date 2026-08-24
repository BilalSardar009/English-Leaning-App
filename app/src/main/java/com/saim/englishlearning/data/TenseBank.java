package com.saim.englishlearning.data;

import com.saim.englishlearning.model.QuizQuestion;
import com.saim.englishlearning.model.Tense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class TenseBank {

    private static final List<Tense> TENSES = new ArrayList<>();

    private TenseBank() {
    }

    public static synchronized List<Tense> all() {
        if (TENSES.isEmpty()) build();
        return Collections.unmodifiableList(TENSES);
    }

    public static Tense get(int index) {
        List<Tense> list = all();
        if (index < 0 || index >= list.size()) return null;
        return list.get(index);
    }

    public static int size() {
        return all().size();
    }

    private static QuizQuestion q(String prompt, String correct, String a, String b, String c, String hint) {
        return new QuizQuestion(prompt, correct, a, b, c, hint);
    }

    private static List<String> list(String... items) {
        return new ArrayList<>(Arrays.asList(items));
    }

    private static void build() {
        TENSES.add(new Tense(
                "Present Simple", "فعل حال سادہ",
                "Subject + base verb (+ s / es for he, she, it)",
                "Habits, routines, facts and timetables.",
                "روزمرہ عادات، معمولات اور حقائق کے لیے۔",
                list("I go to school every day.",
                        "She drinks tea in the morning.",
                        "Water boils at one hundred degrees.",
                        "The train leaves at six."),
                list("میں روز سکول جاتا ہوں۔",
                        "وہ صبح چائے پیتی ہے۔",
                        "پانی سو درجے پر ابلتا ہے۔",
                        "ٹرین چھ بجے روانہ ہوتی ہے۔"),
                list(
                        q("He ____ to the office by bus.", "goes", "go", "going", "gone", "He, she and it take an s."),
                        q("They ____ English every evening.", "practise", "practises", "practising", "practised", "They takes the base verb."),
                        q("The sun ____ in the east.", "rises", "rise", "is rising", "rose", "A fact always uses present simple."),
                        q("My father ____ not like cold weather.", "does", "do", "is", "did", "Use does with he, she and it."),
                        q("____ you understand this rule?", "Do", "Does", "Are", "Did", "Use Do with you."),
                        q("She ____ her homework before dinner.", "finishes", "finish", "finishing", "is finish", "Add es after sh, ch, s and x.")
                )));

        TENSES.add(new Tense(
                "Present Continuous", "فعل حال جاری",
                "Subject + am / is / are + verb + ing",
                "Actions happening right now or around this period.",
                "وہ کام جو اس وقت جاری ہیں۔",
                list("I am reading a book.",
                        "They are playing in the garden.",
                        "She is learning English these days.",
                        "It is raining outside."),
                list("میں کتاب پڑھ رہا ہوں۔",
                        "وہ باغ میں کھیل رہے ہیں۔",
                        "وہ آج کل انگریزی سیکھ رہی ہے۔",
                        "باہر بارش ہو رہی ہے۔"),
                list(
                        q("Look, the baby ____ .", "is sleeping", "sleeps", "slept", "sleep", "Right now means present continuous."),
                        q("We ____ for the bus at the moment.", "are waiting", "wait", "waits", "waited", "Use are with we."),
                        q("I ____ my notes right now.", "am revising", "revise", "revised", "revises", "Use am with I."),
                        q("She ____ dinner at the moment.", "is cooking", "cooks", "cooked", "cook", "Use is with she."),
                        q("They ____ television this evening.", "are watching", "watch", "watches", "watched", "Use are with they."),
                        q("Why ____ you laughing?", "are", "is", "do", "does", "The verb after you is are.")
                )));

        TENSES.add(new Tense(
                "Present Perfect", "فعل حال کامل",
                "Subject + has / have + third form of the verb",
                "Finished actions whose time is not stated, or whose result matters now.",
                "ایسے کام جو مکمل ہو چکے مگر وقت نہیں بتایا گیا۔",
                list("I have finished my work.",
                        "She has visited Karachi twice.",
                        "They have already eaten.",
                        "We have known each other for years."),
                list("میں اپنا کام مکمل کر چکا ہوں۔",
                        "وہ دو بار کراچی جا چکی ہے۔",
                        "وہ پہلے ہی کھانا کھا چکے ہیں۔",
                        "ہم برسوں سے ایک دوسرے کو جانتے ہیں۔"),
                list(
                        q("I ____ my keys, so I cannot open the door.", "have lost", "lost", "am losing", "lose", "The result matters now."),
                        q("She ____ this film three times.", "has seen", "saw", "sees", "seeing", "Use has with she."),
                        q("They ____ not arrived yet.", "have", "has", "did", "are", "Use have with they."),
                        q("We ____ here since 2019.", "have lived", "lived", "live", "are living", "Since needs present perfect."),
                        q("____ you ever eaten Thai food?", "Have", "Has", "Did", "Do", "Use Have with you."),
                        q("He ____ just finished his exam.", "has", "have", "did", "is", "Use has with he.")
                )));

        TENSES.add(new Tense(
                "Present Perfect Continuous", "فعل حال کامل جاری",
                "Subject + has / have + been + verb + ing",
                "An action that started earlier and is still going on.",
                "ایسا کام جو پہلے شروع ہوا اور اب تک جاری ہے۔",
                list("I have been studying since morning.",
                        "She has been waiting for an hour.",
                        "They have been building the road for months.",
                        "It has been raining all day."),
                list("میں صبح سے پڑھ رہا ہوں۔",
                        "وہ ایک گھنٹے سے انتظار کر رہی ہے۔",
                        "وہ مہینوں سے سڑک بنا رہے ہیں۔",
                        "دن بھر بارش ہو رہی ہے۔"),
                list(
                        q("He ____ English for two years.", "has been learning", "learns", "learned", "is learning", "For plus a period needs this tense."),
                        q("They ____ since morning.", "have been working", "work", "worked", "are work", "Use have been with they."),
                        q("I ____ for you since four o'clock.", "have been waiting", "wait", "waited", "am waited", "Since plus a time needs this tense."),
                        q("She ____ that book all week.", "has been reading", "reads", "read", "is read", "Use has been with she."),
                        q("How long ____ you been living here?", "have", "has", "did", "are", "Use have with you."),
                        q("We ____ been practising every evening.", "have", "has", "are", "did", "Use have with we.")
                )));

        TENSES.add(new Tense(
                "Past Simple", "فعل ماضی سادہ",
                "Subject + second form of the verb",
                "A finished action at a stated time in the past.",
                "ماضی میں مکمل ہو جانے والا کام۔",
                list("I went to Lahore last year.",
                        "She wrote a letter yesterday.",
                        "They played cricket on Sunday.",
                        "He did not come to class."),
                list("میں پچھلے سال لاہور گیا۔",
                        "اس نے کل خط لکھا۔",
                        "انہوں نے اتوار کو کرکٹ کھیلی۔",
                        "وہ کلاس میں نہیں آیا۔"),
                list(
                        q("We ____ the match last night.", "watched", "watch", "have watched", "watching", "Last night is a finished time."),
                        q("She ____ to Islamabad in 2020.", "moved", "moves", "has moved", "moving", "A stated past year needs past simple."),
                        q("I ____ him at the station yesterday.", "met", "meet", "have met", "meeting", "Meet becomes met."),
                        q("They ____ not finish the work.", "did", "do", "have", "does", "Use did for past negatives."),
                        q("____ you call me last night?", "Did", "Do", "Have", "Are", "Use Did for past questions."),
                        q("He ____ the whole book in one day.", "read", "reads", "has read", "reading", "In one day states the time.")
                )));

        TENSES.add(new Tense(
                "Past Continuous", "فعل ماضی جاری",
                "Subject + was / were + verb + ing",
                "An action in progress at a past moment, often interrupted.",
                "ماضی کے کسی لمحے میں جاری کام۔",
                list("I was reading when he called.",
                        "They were playing at six o'clock.",
                        "She was cooking all evening.",
                        "We were waiting outside."),
                list("جب اس نے فون کیا تو میں پڑھ رہا تھا۔",
                        "وہ چھ بجے کھیل رہے تھے۔",
                        "وہ سارا شام کھانا پکا رہی تھی۔",
                        "ہم باہر انتظار کر رہے تھے۔"),
                list(
                        q("She ____ when the lights went out.", "was studying", "studied", "studies", "is studying", "An action interrupted in the past."),
                        q("They ____ football at that time.", "were playing", "played", "play", "are playing", "Use were with they."),
                        q("I ____ tea when you rang.", "was making", "made", "make", "am making", "Use was with I."),
                        q("We ____ talking about you.", "were", "was", "are", "did", "Use were with we."),
                        q("What ____ he doing at nine?", "was", "were", "did", "is", "Use was with he."),
                        q("The children ____ sleeping when we arrived.", "were", "was", "are", "have", "Children is plural, so use were.")
                )));

        TENSES.add(new Tense(
                "Past Perfect", "فعل ماضی کامل",
                "Subject + had + third form of the verb",
                "An action finished before another past action.",
                "ماضی کے کسی کام سے بھی پہلے مکمل ہونے والا کام۔",
                list("The train had left before I reached.",
                        "She had finished her work before dinner.",
                        "They had already gone home.",
                        "I had never seen such a place."),
                list("میرے پہنچنے سے پہلے ٹرین جا چکی تھی۔",
                        "اس نے رات کے کھانے سے پہلے کام مکمل کر لیا تھا۔",
                        "وہ پہلے ہی گھر جا چکے تھے۔",
                        "میں نے ایسی جگہ کبھی نہیں دیکھی تھی۔"),
                list(
                        q("He ____ his homework before the guests arrived.", "had finished", "finished", "has finished", "finishes", "The earlier of two past actions."),
                        q("When we reached, the film ____ already started.", "had", "has", "was", "did", "Use had for the earlier action."),
                        q("She said she ____ the letter.", "had posted", "posts", "posting", "post", "Reported speech moves one step back."),
                        q("They ____ never travelled by air before that day.", "had", "have", "has", "were", "Use had for all subjects."),
                        q("I ____ eaten before you called.", "had", "have", "was", "did", "Use had for the first action."),
                        q("The shop ____ closed by the time we got there.", "had", "has", "have", "is", "By the time signals past perfect.")
                )));

        TENSES.add(new Tense(
                "Past Perfect Continuous", "فعل ماضی کامل جاری",
                "Subject + had been + verb + ing",
                "An action that had been going on for a while before another past event.",
                "ماضی کے کسی واقعے سے پہلے ایک عرصے سے جاری کام۔",
                list("I had been waiting for an hour when he arrived.",
                        "She had been teaching for ten years before she retired.",
                        "They had been playing since morning.",
                        "It had been raining before we left."),
                list("جب وہ پہنچا تو میں ایک گھنٹے سے انتظار کر رہا تھا۔",
                        "ریٹائر ہونے سے پہلے وہ دس سال سے پڑھا رہی تھی۔",
                        "وہ صبح سے کھیل رہے تھے۔",
                        "ہمارے نکلنے سے پہلے بارش ہو رہی تھی۔"),
                list(
                        q("He ____ for two hours when the bus came.", "had been waiting", "waited", "has waited", "waits", "A duration before a past event."),
                        q("They ____ been working since dawn.", "had", "have", "has", "were", "Use had been for past duration."),
                        q("She ____ been studying before the power went.", "had", "has", "was", "did", "Use had been here."),
                        q("We ____ been living there for years before we moved.", "had", "have", "are", "did", "Use had been with we."),
                        q("I ____ been trying to call you all morning.", "had", "have", "was", "am", "Past duration before another past point."),
                        q("The team ____ been practising hard before the final.", "had", "have", "has", "were", "Use had been for the earlier stretch.")
                )));

        TENSES.add(new Tense(
                "Future Simple", "فعل مستقبل سادہ",
                "Subject + will + base verb",
                "A decision, promise or prediction about the future.",
                "مستقبل کے بارے میں فیصلہ، وعدہ یا پیش گوئی۔",
                list("I will call you tomorrow.",
                        "She will help you with the form.",
                        "They will arrive by six.",
                        "It will rain tonight."),
                list("میں تمہیں کل فون کروں گا۔",
                        "وہ فارم میں تمہاری مدد کرے گی۔",
                        "وہ چھ بجے تک پہنچ جائیں گے۔",
                        "آج رات بارش ہو گی۔"),
                list(
                        q("I ____ send the file this evening.", "will", "am", "did", "have", "A promise uses will."),
                        q("She ____ be twenty next month.", "will", "is", "was", "has", "A future fact uses will."),
                        q("They ____ not accept this excuse.", "will", "are", "did", "have", "Use will not for future negatives."),
                        q("____ you help me tomorrow?", "Will", "Are", "Did", "Have", "Future questions start with Will."),
                        q("We ____ meet at the library.", "will", "are", "have", "were", "Use will with any subject."),
                        q("He ____ probably come late.", "will", "is", "was", "does", "Predictions use will.")
                )));

        TENSES.add(new Tense(
                "Future Continuous", "فعل مستقبل جاری",
                "Subject + will be + verb + ing",
                "An action that will be in progress at a future moment.",
                "مستقبل کے کسی لمحے میں جاری رہنے والا کام۔",
                list("I will be studying at eight tonight.",
                        "She will be travelling next week.",
                        "They will be waiting for us.",
                        "We will be having lunch at one."),
                list("آج رات آٹھ بجے میں پڑھ رہا ہوں گا۔",
                        "وہ اگلے ہفتے سفر کر رہی ہو گی۔",
                        "وہ ہمارا انتظار کر رہے ہوں گے۔",
                        "ہم ایک بجے کھانا کھا رہے ہوں گے۔"),
                list(
                        q("At ten tomorrow I ____ the exam.", "will be writing", "will write", "wrote", "write", "In progress at a future time."),
                        q("They ____ be flying to Dubai this time next week.", "will", "are", "were", "have", "Use will be plus ing."),
                        q("She ____ be teaching when you arrive.", "will", "is", "was", "did", "Use will be for a future action in progress."),
                        q("We ____ be waiting at the gate.", "will", "are", "were", "have", "Use will be with we."),
                        q("This time tomorrow he ____ be travelling.", "will", "is", "has", "did", "Future continuous marks a moment."),
                        q("____ you be using the car tonight?", "Will", "Are", "Did", "Have", "Questions begin with Will.")
                )));

        TENSES.add(new Tense(
                "Future Perfect", "فعل مستقبل کامل",
                "Subject + will have + third form of the verb",
                "An action that will be finished before a future time.",
                "مستقبل کے کسی وقت سے پہلے مکمل ہو جانے والا کام۔",
                list("I will have finished by Friday.",
                        "She will have left before you arrive.",
                        "They will have built the bridge by then.",
                        "We will have saved enough by December."),
                list("میں جمعے تک کام مکمل کر چکا ہوں گا۔",
                        "تمہارے پہنچنے سے پہلے وہ جا چکی ہو گی۔",
                        "وہ تب تک پل بنا چکے ہوں گے۔",
                        "ہم دسمبر تک کافی بچا چکے ہوں گے۔"),
                list(
                        q("By next year I ____ this course.", "will have completed", "will complete", "completed", "complete", "Finished before a future point."),
                        q("She ____ have gone before we reach.", "will", "is", "was", "has", "Use will have here."),
                        q("They ____ have finished by noon.", "will", "are", "did", "have", "By noon signals future perfect."),
                        q("We ____ have saved enough by then.", "will", "are", "were", "did", "Use will have with we."),
                        q("He will ____ read the whole book by Sunday.", "have", "has", "had", "having", "Will is always followed by have."),
                        q("By the time you call, I ____ have left.", "will", "am", "was", "did", "Use will have for the earlier future action.")
                )));

        TENSES.add(new Tense(
                "Future Perfect Continuous", "فعل مستقبل کامل جاری",
                "Subject + will have been + verb + ing",
                "How long an action will have been going on by a future time.",
                "مستقبل کے کسی وقت تک کام کتنی دیر سے جاری ہو گا۔",
                list("By June I will have been teaching for ten years.",
                        "She will have been waiting for two hours.",
                        "They will have been living here for a decade.",
                        "We will have been travelling all night."),
                list("جون تک میں دس سال سے پڑھا رہا ہوں گا۔",
                        "وہ دو گھنٹے سے انتظار کر رہی ہو گی۔",
                        "وہ ایک دہائی سے یہاں رہ رہے ہوں گے۔",
                        "ہم ساری رات سفر کر رہے ہوں گے۔"),
                list(
                        q("By next month I ____ working here for a year.", "will have been", "will be", "have been", "was", "Duration up to a future point."),
                        q("She will have ____ studying for six hours.", "been", "be", "being", "was", "Will have is followed by been."),
                        q("They ____ have been waiting since noon.", "will", "are", "did", "have", "Use will have been."),
                        q("We will have been ____ for three days by Friday.", "travelling", "travel", "travelled", "travels", "The verb takes ing."),
                        q("By then he ____ have been living abroad for years.", "will", "is", "was", "has", "Use will have been."),
                        q("How long ____ you have been studying by June?", "will", "are", "did", "have", "Questions start with will.")
                )));
    }
}
