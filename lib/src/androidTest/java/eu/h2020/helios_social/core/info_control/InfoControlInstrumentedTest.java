package eu.h2020.helios_social.core.info_control;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import eu.h2020.helios_social.core.context.Context;

/**
 * InfoControl instrumented test, which will execute on an Android device.
 *
 * @see InfoControl
 * @see MessageContext
 */
@RunWith(AndroidJUnit4.class)
public class InfoControlInstrumentedTest {
    @Test
    public void infoControlTest() {

        android.content.Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Initialize repository
        MessageContextRepository repository = new MessageContextRepository(appContext, 100);

        Collection<MessageContext> history = repository.getMessages();
        Log.i("Context", "history size after read:" + history.size());
        repository.deleteAll();

        // Init MyContexts, and add contexts
        MyContexts myContexts = new MyContexts(null, null);
        Context context1 = new Context("context1", false);
        myContexts.add(context1);
        Context context2 = new Context("context2", false);
        myContexts.add(context2);
        Context context3 = new Context("context3", false);
        myContexts.add(context3);

        // Initialize infoControl
        InfoControl infoControl = new InfoControl(myContexts, repository);

        // some values for testing
        String from = "userA";
        long timestamp = System.currentTimeMillis();
        int reactionTime = 10000; // millisec
        int importance = 3;
        float trust = 0.5f;
        String messageTopic = "Helios infocontrol";
        String messageText = "Some text in the context of the infocontrol topic";

        // Then, add training samples. Some received and read messages.
        MessageContext messageContext1 = new MessageContext(context1.getId(), "user1", timestamp, reactionTime, importance, trust, messageTopic, messageText);
        MessageContext messageContext2 = new MessageContext(context2.getId(), "user2", timestamp, reactionTime, 3, trust, "topic2", "Some text");
        MessageContext messageContext3 = new MessageContext(context3.getId(), "user3", timestamp, reactionTime+1000, 1, trust, "test2",
                "message1 context3");
        infoControl.addMessageContext(messageContext1);
        infoControl.addMessageContext(messageContext2);
        infoControl.addMessageContext(messageContext3);

        // Then, suppose that a new message has arrived ...
        // MessageInfo contains data from  the received message
        MessageInfo messageInfo = new MessageInfo("userA", timestamp, messageTopic, messageText);
        // Then, check the context probabilities of the received message ...  (this is an optional phase)
        List<ContextProbability> contextProbabilities = infoControl.getContextProbabilities(messageInfo);
        for(ContextProbability mi : contextProbabilities) {
            Log.i("Context", "Context probability: " + mi.getContext().getName() + ",probability:" + mi.getProbability());
        }
        // Finally, get the importance of the received mnessage in each contexts (of MyContexts)
        List<MessageImportance> messageImportances = infoControl.getMessageImportance(messageInfo);
        for(MessageImportance mi : messageImportances) {
            Log.i("Context", "Message importance: context:" + mi.getContext().getName() + ",importance:" + mi.getImportance());
        }
    }
}
