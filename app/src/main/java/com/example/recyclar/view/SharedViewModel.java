package com.example.recyclar.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.recyclar.model.ToDo;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData<ToDo> newTodo = new MutableLiveData<>();

    public void setNewTodo(ToDo todo) {
        newTodo.setValue(todo);
    }

    public LiveData<ToDo> getNewTodo() {
        return newTodo;
    }
}
