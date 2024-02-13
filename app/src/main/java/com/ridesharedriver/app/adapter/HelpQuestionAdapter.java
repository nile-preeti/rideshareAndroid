package com.ridesharedriver.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ridesharedriver.app.databinding.HelpQuestionHelpRecyclerLayoutBinding;
import com.ridesharedriver.app.pojo.help_question.QuestionData;

import java.util.List;

public class HelpQuestionAdapter extends RecyclerView.Adapter<HelpQuestionAdapter.HelpViewHolder> {

   private List<QuestionData> questionDataList;
   private ItemClicked itemClicked;

    public HelpQuestionAdapter(List<QuestionData> questionDataList, ItemClicked itemClicked) {
        this.questionDataList = questionDataList;
        this.itemClicked= itemClicked;
    }

    @NonNull
    @Override
    public HelpViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HelpViewHolder(HelpQuestionHelpRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HelpViewHolder holder, int position) {

        holder.binding.txtQuestionHelp.setText(questionDataList.get(position).getQuestionCategory());
        holder.binding.goIconHelp.setOnClickListener(e->{
            itemClicked.onItemClicked(Integer.parseInt(questionDataList.get(position).getId()));
        });

    }

    @Override
    public int getItemCount() {
        return questionDataList.size();
    }

    public class HelpViewHolder extends RecyclerView.ViewHolder{
        private HelpQuestionHelpRecyclerLayoutBinding binding;
        public HelpViewHolder(HelpQuestionHelpRecyclerLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public interface ItemClicked{
        void onItemClicked(int id);
    }
}
