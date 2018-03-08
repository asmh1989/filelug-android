package com.filelug.android.ui.adapter;

import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDAdapter;
import com.filelug.android.R;
import com.filelug.android.ui.model.CustomMaterialSimpleListItem;

import java.util.ArrayList;
import java.util.List;

// Copy from MaterialDialogs v.0.9.0.2, com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter
public class CustomMaterialSimpleListAdapter extends RecyclerView.Adapter<CustomMaterialSimpleListAdapter.CustomSimpleListVH> implements MDAdapter {

	public interface Callback {
		//Change here!
		//void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item);
		void onMaterialListItemSelected(MaterialDialog dialog, int index, CustomMaterialSimpleListItem item);
	}

	private MaterialDialog dialog;
	//Change here!
	//private List<MaterialSimpleListItem> mItems;
	private List<CustomMaterialSimpleListItem> mItems;
	private Callback mCallback;

	public CustomMaterialSimpleListAdapter(Callback callback) {
	//Change here!
	//public MaterialSimpleListAdapter(Callback callback) {
		mItems = new ArrayList<>(4);
		mCallback = callback;
	}

	//Change here!
	//public void add(MaterialSimpleListItem item) {
	public void add(CustomMaterialSimpleListItem item) {
		mItems.add(item);
		notifyItemInserted(mItems.size() - 1);
	}

	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}

	//Change here!
	//public MaterialSimpleListItem getItem(int index) {
	public CustomMaterialSimpleListItem getItem(int index) {
		return mItems.get(index);
	}

	@Override
	public void setDialog(MaterialDialog dialog) {
		this.dialog = dialog;
	}

	@Override
	public CustomSimpleListVH onCreateViewHolder(ViewGroup parent, int viewType) {
	//Change here!
	//public SimpleListVH onCreateViewHolder(ViewGroup parent, int viewType) {
		final View view = LayoutInflater.from(parent.getContext())
				//Change here!
				//.inflate(R.layout.md_simplelist_item, parent, false);
				.inflate(R.layout.rowitem_folderchooser, parent, false);
		return new CustomSimpleListVH(view, this);
	}

	@Override
	public void onBindViewHolder(CustomSimpleListVH holder, int position) {
	//Change here!
	//public void onBindViewHolder(SimpleListVH holder, int position) {
		if (dialog != null) {
			//Change here!
			//final MaterialSimpleListItem item = mItems.get(position);
			final CustomMaterialSimpleListItem item = mItems.get(position);
			if (item.getIcon() != null) {
				holder.icon.setImageDrawable(item.getIcon());
				holder.icon.setPadding(item.getIconPadding(), item.getIconPadding(),
						item.getIconPadding(), item.getIconPadding());
				//Change here!
				//holder.icon.getBackground().setColorFilter(item.getBackgroundColor(),
				//		PorterDuff.Mode.SRC_ATOP);
				if ( holder.icon.getBackground() != null ) {
					holder.icon.getBackground().setColorFilter(item.getBackgroundColor(),
							PorterDuff.Mode.SRC_ATOP);
				}
			} else {
				holder.icon.setVisibility(View.GONE);
			}
			holder.title.setTextColor(dialog.getBuilder().getItemColor());
			holder.title.setText(item.getContent());
			dialog.setTypeface(holder.title, dialog.getBuilder().getRegularFont());
		}
	}

	@Override
	public int getItemCount() {
		return mItems.size();
	}

	//Change here!
	//public static class SimpleListVH extends RecyclerView.ViewHolder implements View.OnClickListener {
	public static class CustomSimpleListVH extends RecyclerView.ViewHolder implements View.OnClickListener {

		final ImageView icon;
		final TextView title;
		//Change here!
		//final MaterialSimpleListAdapter adapter;
		final CustomMaterialSimpleListAdapter adapter;

		//Change here!
		//public SimpleListVH(View itemView, MaterialSimpleListAdapter adapter) {
		public CustomSimpleListVH(View itemView, CustomMaterialSimpleListAdapter adapter) {
			super(itemView);
			icon = (ImageView) itemView.findViewById(android.R.id.icon);
			title = (TextView) itemView.findViewById(android.R.id.title);
			this.adapter = adapter;
			itemView.setOnClickListener(this);
		}

		@Override
		public void onClick(View view) {
			if (adapter.mCallback != null)
				adapter.mCallback.onMaterialListItemSelected(adapter.dialog, getAdapterPosition(), adapter.getItem(getAdapterPosition()));
		}
	}

}
