package tkmce.hestia20.user_dash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import tkmce.hestia20.R;

public class BottomSheetLogoutFragment extends BottomSheetDialogFragment {
    private logoutListener listener;

    public static BottomSheetLogoutFragment newInstance(logoutListener listener) {
        BottomSheetLogoutFragment fragment = new BottomSheetLogoutFragment();
        fragment.listener = listener;
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
        View view = inflater.inflate(R.layout.logout_prompt_sheet, container, false);

        view.findViewById(R.id.no_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.dismissSheet();
            }
        });

        view.findViewById(R.id.yes_logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLogout();
            }
        });

        return view;
    }

    public interface logoutListener {
        void onLogout();

        void dismissSheet();
    }
}