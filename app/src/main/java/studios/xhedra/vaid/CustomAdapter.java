package studios.xhedra.vaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class CustomAdapter  extends BaseAdapter {
    public ArrayList<Integer> selected = new ArrayList<>();
    private Context context;
    public static ArrayList<Symptom> SymAL;


    public CustomAdapter(Context context, ArrayList<Symptom> sal) {

        this.context = context;
        SymAL = sal;

    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }
    public ArrayList<Integer> getSelectedList(){
        return selected;
    }

    @Override
    public int getCount() {
        return SymAL.size();
    }

    @Override
    public Symptom getItem(int position) {
        return SymAL.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder(); LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_item, null, true);

            holder.checkBox = (CheckBox) convertView.findViewById(R.id.cb);
            holder.tvAnimal = (TextView) convertView.findViewById(R.id.animal);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }


        holder.checkBox.setText("Checkbox " + position);
        holder.tvAnimal.setText(SymAL.get(position).getAnimal());

        holder.checkBox.setChecked(SymAL.get(position).getSelected());

        holder.checkBox.setTag(R.integer.btnplusview, convertView);
        holder.checkBox.setTag( position);
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View tempview = (View) holder.checkBox.getTag(R.integer.btnplusview);
                TextView tv = (TextView) tempview.findViewById(R.id.animal);
                Integer pos = (Integer)  holder.checkBox.getTag();
                //Toast.makeText(context, "Checkbox "+pos+" clicked!", Toast.LENGTH_SHORT).show();

                if(SymAL.get(pos).getSelected()){
                    selected.remove(pos);
                    SymAL.get(pos).setSelected(false);
                }else {
                    selected.add(pos);
                    SymAL.get(pos).setSelected(true);
                }

            }
        });

        return convertView;
    }

    private class ViewHolder {

        protected CheckBox checkBox;
        private TextView tvAnimal;

    }

}