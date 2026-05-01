package net.kryos.notification;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.kryos.Kryos;

public class NotificationBus {
    private final List<Notification> stack = new LinkedList<>();

    public void post(Notification notification) {
        stack.add(0, notification);

        while (stack.size() > Kryos.featureManager.notifications.max.getValue()) {
            stack.remove(stack.size() - 1);
        }
    }

    public void update() {
        long now = System.currentTimeMillis();

        Iterator<Notification> it = stack.iterator();
        while (it.hasNext()) {
            Notification n = it.next();
            if (now - n.getCreatedAt() >= Kryos.featureManager.notifications.lifetime.getValue()) {
                it.remove();
            }
        }
    }

    public List<Notification> getActive() {
        return stack;
    }
}
