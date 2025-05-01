package hp.cliofy.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;

import java.util.List;

import hp.cliofy.Item.Item;
import hp.cliofy.R;

public class ItemAdapter<T extends Item> extends ArrayAdapter<T> {
    public ItemAdapter(Context context, List<T> data) {
        super(context, 0, data);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Item item = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ImageView itemImage = convertView.findViewById(R.id.itemImage);
        TextView itemName = convertView.findViewById(R.id.itemName);

        Glide.with(this.getContext()).load(item.getImageUrl()).into(itemImage);
        itemName.setText(item.toString());

        return convertView;
    }
}
