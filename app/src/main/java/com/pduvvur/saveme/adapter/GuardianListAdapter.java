package com.pduvvur.saveme.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.pduvvur.saveme.R;
import com.pduvvur.saveme.db.GuardiansDataSource;
import com.pduvvur.saveme.guardian.Guardian;

import java.util.List;

/**
 * Created by PradeepKumar on 7/19/2015.
 */
public class GuardianListAdapter extends BaseAdapter
{
    private Context m_context;
    private Activity m_activity;
    protected List<Guardian> m_guardianList;
    private LayoutInflater m_layoutInflater;

    // declare the color generator and drawable builder
    private ColorGenerator m_colorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder m_drawableBuilder;

    public GuardianListAdapter(Context context, List<Guardian> guardiansList,
                               Activity activity)
    {
        m_context = context;
        m_activity = activity;
        m_guardianList = guardiansList;
        m_layoutInflater = LayoutInflater.from(context);
        m_drawableBuilder = TextDrawable.builder().round();
    }

    @Override
    public int getCount()
    {
        return m_guardianList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return m_guardianList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = m_layoutInflater.inflate(R.layout.list_item, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Guardian guardian = m_guardianList.get(position);
        // Get the first char of the name to display in the image.
        String firstChar;
        if(guardian.getName().length() > 0){
            firstChar = String.valueOf(guardian.getName().charAt(0)).toUpperCase();
        } else {
            firstChar = " ";
        }
        // Build the image
        TextDrawable drawable = m_drawableBuilder.build(firstChar,
                m_colorGenerator.getColor(guardian.getName()));

        holder.m_numberView.setText(guardian.getPhoneNumber());
        holder.m_nameView.setText(guardian.getName());
        holder.m_guardianImage.setImageDrawable(drawable);
        // Delete button for a list item
        holder.m_deleteGuardianButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final GuardiansDataSource guardiansDataSource =
                                new GuardiansDataSource(m_context);
                        guardiansDataSource.open();
                        // Delete guardian
                        final int numRowsDeleted = guardiansDataSource.
                                deleteGuardian(guardian);

                        m_activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(numRowsDeleted == 1){
                                    m_guardianList.remove(position);
                                    notifyDataSetChanged();
                                    guardiansDataSource.close();
                                    Toast.makeText(m_context, "Guardian deleted!",
                                            Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(m_context, "Error deleting guardian",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }).start();
            }
        });

        return convertView;
    }

    private class ViewHolder
    {
        private View m_view;
        private ImageView m_guardianImage;
        private TextView m_nameView;
        private TextView m_numberView;
        private ImageButton m_deleteGuardianButton;

        private ViewHolder(View view)
        {
            m_view = view;
            m_guardianImage = (ImageView) view.findViewById(R.id.image_view);
            m_nameView = (TextView) view.findViewById(R.id.name_text_view);
            m_numberView = (TextView) view.findViewById(R.id.number_text_view);
            m_deleteGuardianButton = (ImageButton) view.findViewById(R.id.delete_guardian);
        }
    }
}
