package com.ste.zk.gritter;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.json.simple.JSONObject;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Matteo Barbieri <m.barbieri@ste-energy.com>
 */
public final class Gritter {

    /**
     * Default fade-out time for notifications.
     */
    public static final int DEFAULT_TIME = 6000;

    /**
     * Private constructor
     */
    private Gritter() {
    }

    /**
     * Represents a single Gritter notification.
     */
    public static class Notification implements Serializable {
        private static final long serialVersionUID = -1345910234654400143L;

        /**
         * Notification title.
         */
        private String title;
        /**
         * Notification text.
         */
        private String text;
        /**
         * (Optional) Image source URL
         */
        private String image;
        /**
         * Is the notification sticky?
         */
        private boolean sticky;
        /**
         * Time before notification automatically fades out
         */
        private int time;
        /**
         * (Optional) CSS class for the notification
         */
        private String sclass;

        public Notification(String title, String text, String image, boolean sticky, int time, String sclass) {
            this.title = title;
            this.text = text;
            this.image = image;
            this.sticky = sticky;
            this.time = time;
            this.sclass = sclass;
        }

        public String toJSONString() {
            Map<String, Object> json = Maps.newLinkedHashMap();

            json.put("title", title);
            json.put("text", text);

            if (!Strings.isNullOrEmpty(image))
                json.put("image", Executions.encodeURL(image));
            if (sticky)
                json.put("sticky", sticky);
            if (time != DEFAULT_TIME)
                json.put("time", time);
            if (!Strings.isNullOrEmpty(sclass))
                json.put("class_name", sclass);

            return JSONObject.toJSONString(json);
        }
    }

    public static class NotificationBuilder {
        private String title;
        private String text;
        private String image;
        private boolean sticky;
        private int time = DEFAULT_TIME;
        private String sclass;

        /**
         * Private constructor
         */
        private NotificationBuilder() {
        }

        /**
         * Sets the notification title
         *
         * @param title non-null and non-empty string
         * @return this builder
         * @throws NullPointerException     if title is null
         * @throws IllegalArgumentException if title is empty
         */
        public NotificationBuilder withTitle(String title) {
            Preconditions.checkNotNull(title, "title cannot be null");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(title), "title cannot be empty");
            this.title = title;
            return this;
        }

        /**
         * Sets the notification text
         *
         * @param text non-null and non-empty string
         * @return this builder
         * @throws NullPointerException     if text is null
         * @throws IllegalArgumentException if text is empty
         */
        public NotificationBuilder withText(String text) {
            Preconditions.checkNotNull(text, "text cannot be null");
            Preconditions.checkArgument(!Strings.isNullOrEmpty(text), "text cannot be empty");
            this.text = text;
            return this;
        }

        /**
         * Sets the notification image.
         *
         * @param image url as string
         * @return this builder
         */
        public NotificationBuilder withImage(String image) {
            this.image = image;
            return this;
        }

        /**
         * Sets whether the notification should auto-fade or stick until it is removed programmatically or by the user.
         *
         * @param sticky sticky flag
         * @return this builder
         */
        public NotificationBuilder withSticky(boolean sticky) {
            this.sticky = sticky;
            return this;
        }

        /**
         * Sets the auto-fade timeout.
         *
         * @param time positive integer
         * @return this builder
         * @throws IllegalArgumentException if time is negative
         */
        public NotificationBuilder withTime(int time) {
            Preconditions.checkArgument(time >= 0, "time must be positive");
            this.time = time;
            return this;
        }

        /**
         * Sets the CSS class.
         *
         * @param sclass class string
         * @return this builder
         */
        public NotificationBuilder withSclass(String sclass) {
            this.sclass = sclass;
            return this;
        }

        /**
         * Builds the notification. Title and text must be set prior building.
         *
         * @return built notification
         * @throws IllegalStateException if title or text are not set
         */
        public Notification build() {
            Preconditions.checkState(!Strings.isNullOrEmpty(title), "title has to be set");
            Preconditions.checkState(!Strings.isNullOrEmpty(text), "text has to be set");
            return new Notification(title, text, image, sticky, time, sclass);
        }

        /**
         * Shows a notification.
         * <p/>
         * Must be called in a Servlet thread with an active ZK execution. Shortcut to Gritter.show(this.build());
         */
        public void show() {
            Gritter.show(this.build());
        }
    }

    /**
     * Creates a notification builder.
     *
     * @return new notification builder
     */
    public static NotificationBuilder notification() {
        return new NotificationBuilder();
    }

    /**
     * Shows a notification.
     * <p/>
     * Must be called in a Servlet thread with an active ZK execution.
     *
     * @param notification notification to be shown
     */
    public static void show(Notification notification) {
        Preconditions.checkNotNull(notification, "notification cannot be null");
        Clients.evalJavaScript("$.gritter.add(" + notification.toJSONString() + ")");
    }

    /**
     * Removes all remaining notifications.
     * <p/>
     * Must be called in a Servlet thread with an active ZK execution.
     */
    public static void removeAll() {
        Clients.evalJavaScript("$.gritter.removeAll()");
    }
}
