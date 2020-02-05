package tkmce.hestia20.user_dash;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import tkmce.hestia20.R;

public class BottomSheetLogoutFragment extends BottomSheetDialogFragment {

    private View view;
    public static BottomSheetLogoutFragment fragment;
    private Context context;

    private BottomSheetLogoutFragment(Context context) {
        this.context = context;
    }

    public static BottomSheetLogoutFragment newInstance(Context context) {
        Bundle args = new Bundle();
        fragment = new BottomSheetLogoutFragment(context);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.logout_prompt_sheet, container, false);

        view.findViewById(R.id.no_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragment.dismiss();
            }
        });

        view.findViewById(R.id.yes_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Implement Logout Option
            }
        });

        return view;
    }
}