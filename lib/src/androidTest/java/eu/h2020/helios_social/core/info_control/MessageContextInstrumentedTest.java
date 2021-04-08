package eu.h2020.helios_social.core.info_control;

import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.List;

import eu.h2020.helios_social.core.context.Context;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MessageContextInstrumentedTest {
    @Test
    public void messageContextHistoryRepositoryTest() {

        android.content.Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        Log.i("Context", "package:" + appContext.getPackageName());
        assertEquals("eu.h2020.helios_social.core.test", appContext.getPackageName());

        MessageContextRepository repository = new MessageContextRepository(appContext, 100);

        Collection<MessageContext> history = repository.getMessages();
        Log.i("Context", "history size after read:" + history.size());
        repository.deleteAll();

        String contextID = "context1";
        String from = "userA";
        long timestamp = System.currentTimeMillis();
        int reactionTime = 10000; // millisec
        int importance = 3;
        float trust = 0.5f;
        String messageTopic = "Context topic1";
        String messageText = "Some text about Context topic";
        MessageContext messageContext = new MessageContext(contextID, from, timestamp, reactionTime, importance, trust, messageTopic, messageText);
        repository.insert(messageContext);
        MessageContext messageContext2 = new MessageContext(contextID, from, timestamp, 10, importance, trust, messageTopic, messageText);
        repository.insert(messageContext2);
        MessageContext messageContext3 = new MessageContext(contextID, from, timestamp, 2000, importance, trust, messageTopic, messageText);
        repository.insert(messageContext3);
        MessageContext messageContext4 = new MessageContext(contextID, from, timestamp, 100, importance, trust, messageTopic, messageText);
        repository.insert(messageContext4);
        MessageContext messageContext5 = new MessageContext(contextID, from, timestamp, 200, importance, trust, messageTopic, messageText);
        repository.insert(messageContext5);

        history = repository.getMessages();
        Log.i("Context", "history size:" + history.size());
        for (MessageContext mc : history) {
            Log.i("Context", "Context id:" + mc.getContextID());
            assertEquals(contextID, mc.getContextID());
        }
        history = repository.getMessagesByContextFrom(contextID, from);
        for (MessageContext mc : history) {
            Log.i("Context", "Context id:" + mc.getContextID() + "," + mc.getFrom());
           //  assertEquals(contextID, mc.getContextID());
        }

        Log.i("Context", "reactionTime=" + repository.getMedianReactionTime(contextID, from));
        Log.i("Context", "importance=" + repository.getMedianImportance(contextID, from));
        Log.i("Context", "trust=" + repository.getMedianTrust(contextID, from));
        // repository.deleteAll();
        history = repository.getMessages();
        Log.i("Context", "history size:" + history.size());
        
        MyContexts myContexts = new MyContexts(null);
        Context context1 = new Context("context1", false);
        myContexts.add(context1);
        Context context2 = new Context("context2", false);
        myContexts.add(context2);
        Context context3 = new Context("context3", false);
        myContexts.add(context3);
        InfoControl infoControl = new InfoControl(myContexts, repository);
        MessageInfo messageInfo = new MessageInfo(from, timestamp, messageTopic, messageText);
        messageContext = new MessageContext(context1.getId(), from, timestamp, reactionTime, importance, trust, messageTopic, messageText);
        messageContext2 = new MessageContext(context2.getId(), from, timestamp, reactionTime, 5, 1, messageTopic, messageText);
        messageContext3 = new MessageContext(context3.getId(), "user3", timestamp, reactionTime+1000, importance, trust, "test2", "message1 context3");
        infoControl.addMessageContext(messageContext);
        infoControl.addMessageContext(messageContext2);
        infoControl.addMessageContext(messageContext3);
        List<ContextProbability> contextProbabilities = infoControl.getContextProbabilities(messageInfo);
        for(ContextProbability mi : contextProbabilities) {
            Log.i("Context", "Context probability: " + mi.getContext().getName() + ",probability:" + mi.getProbability());
        }
        List<MessageImportance> messageImportances = infoControl.getMessageImportance(messageInfo);
        Log.i("Context", "importances size:" + messageImportances.size() );
        for(MessageImportance mi : messageImportances) {
            Log.i("Context", "Message importance: context:" + mi.getContext().getName() + ",importance:" + mi.getImportance());
        }
        Log.i("Context", "importance context=" + infoControl.getMessageImportance(messageInfo, context1 ));
    }
}
