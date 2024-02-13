package com.ridesharedriver.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ridesharedriver.app.databinding.HelpQuestionHelpRecyclerLayoutBinding;
import com.ridesharedriver.app.pojo.help_question.QuestionCategoryData;

import java.util.List;

public class HelpQuestionCategoryAdapter extends RecyclerView.Adapter<HelpQuestionCategoryAdapter.QuestionCategoryViewHolder> {

    private List<QuestionCategoryData> questionCategoryDataList;
    private ItemClicked itemClicked;

    public HelpQuestionCategoryAdapter(List<QuestionCategoryData> questionCategoryDataList, ItemClicked itemClicked) {
        this.questionCategoryDataList = questionCategoryDataList;
        this.itemClicked = itemClicked;
    }

    @NonNull
    @Override
    public QuestionCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuestionCategoryViewHolder(HelpQuestionHelpRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionCategoryViewHolder holder, int position) {
        holder.binding.txtQuestionHelp.setText(questionCategoryDataList.get(position).getQuestion().trim());
        if(questionCategoryDataList.get(position).getId().equalsIgnoreCase("1"))
        {
            holder.binding.goIconHelp.setVisibility(View.GONE);
        }
        holder.binding.goIconHelp.setOnClickListener(e->{
            itemClicked.onItemClicked(Integer.parseInt(questionCategoryDataList.get(position).getId()),questionCategoryDataList.get(position).getQuestion(),questionCategoryDataList.get(position).getEmail());
        });
    }

    @Override
    public int getItemCount() {
        return questionCategoryDataList.size();
    }

    public class QuestionCategoryViewHolder extends RecyclerView.ViewHolder{

        private HelpQuestionHelpRecyclerLayoutBinding binding;
        public QuestionCategoryViewHolder(HelpQuestionHelpRecyclerLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


    public interface ItemClicked{
        void onItemClicked(int id, String question, String email);
    }
}
