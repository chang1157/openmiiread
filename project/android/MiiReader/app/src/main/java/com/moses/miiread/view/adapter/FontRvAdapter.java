package com.moses.miiread.view.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.moses.miiread.R;
import com.moses.miiread.help.ReadBookControl;

import java.io.File;

public class FontRvAdapter extends RecyclerView.Adapter<FontRvAdapter.VHolder> {
    private FontFile[] fontFiles;
    private Context context;
    private OnFontActionItemClick onFontActionItemClick;
    private ReadBookControl readBookControl;

    public FontRvAdapter(Context context, @NonNull FontFile[] fontFiles) {
        this.context = context;
        this.fontFiles = fontFiles;
        this.readBookControl = ReadBookControl.getInstance();
    }

    public FontRvAdapter setOnFontActionItemClick(OnFontActionItemClick onFontActionItemClick) {
        this.onFontActionItemClick = onFontActionItemClick;
        return this;
    }

    @NonNull
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_font_rv, parent, false);
        return new VHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {
        FontFile fontFile = fontFiles[position];
        if (position == 0)
            holder.lMargin.setVisibility(View.VISIBLE);
        else holder.lMargin.setVisibility(View.GONE);
        if (position != fontFiles.length - 1)
            holder.rMargin.setVisibility(View.VISIBLE);
        else
            holder.rMargin.setVisibility(View.GONE);
        if (fontFile.fontAction == FontAction.setFont) {
            Typeface typeface = Typeface.createFromFile(fontFile.font);
            holder.fontEg.setTypeface(typeface);
            holder.fontEg.setText("A");
            holder.fontTxt.setText(fontFile.font.getName().substring(0, fontFile.font.getName().indexOf(".")));
        } else if (fontFile.fontAction == FontAction.resetFont) {
            holder.fontEg.setText("A");
            holder.fontTxt.setText(context.getResources().getString(R.string.default_font));
            holder.fontEg.setTypeface(Typeface.DEFAULT);
        } else {
            holder.fontEg.setText("+");
            holder.fontTxt.setText(context.getResources().getString(R.string.add_fonts));
            holder.fontEg.setTypeface(Typeface.DEFAULT);
        }
        holder.fontEg.setOnClickListener(v -> onFontActionItemClick.onItemClick(fontFile, position));
        if (readBookControl.getFontPath() == null) {
            if (fontFile.fontAction == FontAction.resetFont) {
                holder.fontEg.setBackgroundResource(R.drawable.bg_font_checked);
                holder.fontEg.setTextColor(context.getResources().getColor(R.color.background));
                holder.fontTxt.setTextColor(context.getResources().getColor(R.color.colorControlActivated));
            } else {
                holder.fontEg.setBackgroundResource(R.drawable.bg_font_unchecked);
                holder.fontEg.setTextColor(context.getResources().getColor(R.color.tv_text_summary));
                holder.fontTxt.setTextColor(context.getResources().getColor(R.color.tv_text_summary));
            }
        } else if (fontFile.fontAction == FontAction.setFont && new File(readBookControl.getFontPath())
                .getAbsolutePath().compareTo(fontFile.font.getAbsolutePath()) == 0) {
            holder.fontEg.setBackgroundResource(R.drawable.bg_font_checked);
            holder.fontEg.setTextColor(context.getResources().getColor(R.color.background));
            holder.fontTxt.setTextColor(context.getResources().getColor(R.color.colorControlActivated));
        } else {
            holder.fontEg.setBackgroundResource(R.drawable.bg_font_unchecked);
            holder.fontEg.setTextColor(context.getResources().getColor(R.color.tv_text_summary));
            holder.fontTxt.setTextColor(context.getResources().getColor(R.color.tv_text_summary));
        }
    }

    @Override
    public int getItemCount() {
        return fontFiles.length;
    }

    public static class VHolder extends RecyclerView.ViewHolder {

        TextView fontEg, fontTxt;
        View lMargin, rMargin;

        public VHolder(@NonNull View itemView) {
            super(itemView);
            fontEg = itemView.findViewById(R.id.font_eg);
            fontTxt = itemView.findViewById(R.id.font_txt);
            lMargin = itemView.findViewById(R.id.lmargin);
            rMargin = itemView.findViewById(R.id.rmargin);
        }
    }

    public interface OnFontActionItemClick {
        void onItemClick(FontFile fontFile, int posi);
    }

    public static class FontFile {
        public File font;
        public FontAction fontAction;

        public FontFile(FontAction fontAction, File font) {
            this.fontAction = fontAction;
            this.font = font;
        }
    }

    public enum FontAction {
        setFont,
        resetFont,
        addFont;
    }
}
