package app.seamansbook.tests.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.seamansbook.tests.R;
import app.seamansbook.tests.models.NotificationItem;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<NotificationItem> notificationItems;
    private OnNotificationClickListener listener;
    private Context context;

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationItem item);
    }


    public NotificationsAdapter(Context context, List<NotificationItem> notificationItems, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationItems = notificationItems;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_notification_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        NotificationItem item = notificationItems.get(position);
        holder.title.setText(item.getTitle());
        holder.time.setText(item.getDate());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(item);
                holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.notifications_viewed_foreground)));
            }
        });

        if(item.getEmailType().equals("update")){
            holder.notification_icon.setImageResource(R.drawable.icon__notification_update);
        }

        if(item.getViewed() == 1) {
            holder.itemView.setForeground(new ColorDrawable(ContextCompat.getColor(context, R.color.notifications_viewed_foreground)));

            holder.title.setTextColor(ContextCompat.getColor(context, R.color.notifications_viewed_text_color));
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.notifications_viewed_text_color));
            holder.notification_icon.setColorFilter(ContextCompat.getColor(context, R.color.notifications_viewed_text_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public int getItemCount() {
        return notificationItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView time;
        public ImageView notification_icon;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.notification_title);
            time = view.findViewById(R.id.notification_time);
            notification_icon = view.findViewById(R.id.notification_icon);
        }
    }
}
