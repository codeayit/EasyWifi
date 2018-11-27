package com.robot.brain.sem;

import com.ayit.klog.KLog;
import com.robot.brain.BrainApplication;

import java.util.LinkedList;

/**
 * Created by lny on 2018/1/12.
 */

public class ActionConsumer {


    private LinkedList<Conversation> conversations = new LinkedList<>();
    private final int limitSize = 1;

    private static ActionConsumer consumer;

    public static ActionConsumer getInstance() {
        if (consumer == null) synchronized (ActionConsumer.class) {
            consumer = new ActionConsumer();
        }
        return consumer;
    }

    public AppAction pollAppAction() {
        LinkedList<Conversation> temp = new LinkedList<>();
        for (Conversation conversation : conversations) {
            if (!conversation.isEmpty()) {
                temp.addLast(conversation);
            }
        }
        conversations.clear();
        conversations.addAll(temp);
        if (conversations.size() > 0) {
            AppAction appAction = conversations.getFirst().removeFirst();
            if (conversations.getFirst().isEmpty()) {
                appAction.setLast(true);
            }
            return appAction;
        }
        return null;
    }


    public void addConversation(Conversation conversation) {
        KLog.d("addConversation before : " + conversations.size());
        if (conversations.size() > 0) {
            Conversation first = conversations.getFirst();
            if (first.isKeep() && !first.isEmpty()) {

            } else {
                stopConversation();
            }
            if (conversations.size() == limitSize) {
                Conversation last = conversations.removeLast();
            }
        }
        conversations.addFirst(conversation);
    }

    public void stopConversation() {
        if (conversations.size() > 0) {
            Conversation conversation = conversations.removeFirst();
//            if (!conversation.isKeep())
        }
    }
    public boolean isEmpty() {
        return conversations.isEmpty() || conversations.getFirst().isEmpty();
    }

    public void excute() {
        AppAction appAction = pollAppAction();
        if (appAction != null) {
            KLog.d("current appaction : "+appAction.toJSONString());
            switch (appAction.getAppType()) {
                case AppAction.TYPE_EXTERNAL:
                    new ExternalAppConsumer(appAction,BrainApplication.getInstance()).excute();
                    break;
                case AppAction.TYPE_INTERNAL:
                    new InternalAppConsumer(appAction,BrainApplication.getInstance()).excute();
                    break;
            }
        } else {
            KLog.d("current appaction is null");
        }
    }


}
