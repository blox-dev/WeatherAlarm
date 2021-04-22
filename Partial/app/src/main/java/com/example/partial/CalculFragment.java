package com.example.partial;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CalculFragment extends AppCompatDialogFragment {
    int result;
    public CalculFragment(int result) {
        this.result = result;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Rezultat: " + this.result)
                .setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, id) -> {

                })
                .setNegativeButton("Share", (DialogInterface.OnClickListener) (dialog, id) -> {
                    Intent intent = new Intent(getContext(), ShareActivity.class);
                    startActivity(intent);
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}