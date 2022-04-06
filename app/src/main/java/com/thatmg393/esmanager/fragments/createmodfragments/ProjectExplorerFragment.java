package com.thatmg393.esmanager.fragments.createmodfragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.CreateModActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.adapters.TextAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectExplorerFragment extends Fragment {
    private final String rootPath = CreateModActivity.pp;

    private TextAdapter textAdapter;
    private File[] modPathFilesnFolder;
    private List<String> pathLists;
    private ListView explorer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_explorer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            if (Utils.ActivityUtils.arePermissionsDenied(getActivity().getApplicationContext(), Utils.app_perms)) {
                getActivity().requestPermissions(Utils.app_perms, 69418);
                explorerInit();
            } else {
                explorerInit();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(textAdapter.getItem(info.position).substring(textAdapter.getItem(info.position).lastIndexOf('/') + 1));

        menu.add(1, Menu.FIRST, Menu.FIRST, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                newDialog(getContext(), "Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFolderOrFile(modPathFilesnFolder[info.position]);
                    }
                }, "No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }, "Warning!", "Are you sure you want to delete this file?\r\nThis cannot be undone!");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void explorerInit() {
        explorer = requireView().findViewById(R.id.project_explorer);
        registerForContextMenu(explorer);

        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(getActivity().getMainLooper());

        executor.execute(() -> {
            File modRootDir = new File(rootPath);

            modPathFilesnFolder = modRootDir.listFiles();

            pathLists = new ArrayList<>();

            for (int f = 0; f < modPathFilesnFolder.length; f++) {
                pathLists.add(modPathFilesnFolder[f].getAbsolutePath());
            }

            handler.post(() -> {
                textAdapter = new TextAdapter();
                textAdapter.setData(pathLists);

                explorer.setAdapter(textAdapter);

                boolean[] selection = new boolean[pathLists.size()];

                explorer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        selection[position] = !selection[position];
                        textAdapter.setSelection(selection);

                        boolean isOneItemSelected = false;
                        boolean isMultipleItemSelected = false;

                        for (boolean aSelection : selection) {
                            if (aSelection) {
                                isOneItemSelected = true;
                                break;
                            }
                        }

                        //Check for MultiItem selection
                        int s = 0;
                        for (boolean aSelection : selection) {
                            if (aSelection) {
                                s++;
                                if (s > 2) {
                                    isMultipleItemSelected = true;
                                    break;
                                }
                            }
                        }

                        if (isOneItemSelected && !isMultipleItemSelected) {
                            requireView().findViewById(R.id.explorer_bottomBar_singleItem).setVisibility(View.VISIBLE);
                        } else {
                            requireView().findViewById(R.id.explorer_bottomBar_singleItem).setVisibility(View.GONE);
                        }

                        if (isMultipleItemSelected && isOneItemSelected) {
                            requireView().findViewById(R.id.explorer_bottomBar_multiItem).setVisibility(View.VISIBLE);
                        } else {
                            requireView().findViewById(R.id.explorer_bottomBar_multiItem).setVisibility(View.GONE);
                        }

                        return false;
                    }
                }); // onItemLongClickListener
            }); // handler.post()
        }); // executor.execute()
    }

    private void deleteFolderOrFile(File fileToDelete) {
        if (fileToDelete.isDirectory())
        {
            try {
                String[] children = fileToDelete.list();
                for (String child : children) {
                    new File(fileToDelete, child).delete();
                }
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                fileToDelete.delete();
            }
        } else if (fileToDelete.isFile()) {
            fileToDelete.delete();
        }
    }

    private void newDialog(@NonNull Context context,
                           @NonNull CharSequence positiveButText,
                           @NonNull DialogInterface.OnClickListener positiveButListener,
                           @NonNull CharSequence negativeButText,
                           @NonNull DialogInterface.OnClickListener negativeButListener,
                           @Nullable CharSequence title,
                           @Nullable CharSequence message) {
        AlertDialog adb = new AlertDialog.Builder(context).create();

        adb.setTitle(title);
        adb.setMessage(message);

        adb.setButton(DialogInterface.BUTTON_POSITIVE, positiveButText, positiveButListener);
        adb.setButton(DialogInterface.BUTTON_NEGATIVE, negativeButText, negativeButListener);

        adb.show();
    }

    private void PickFile() {
        /* Disabled for some reason.
        SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(getApplicationContext(),
                () -> Toast.makeText(getApplicationContext(), "Canceled!!", Toast.LENGTH_SHORT).show(),
                files -> Toast.makeText(getApplicationContext(), files[0].getPath(), Toast.LENGTH_SHORT).show());
        singleFilePickerDialog.show();
         */
    }

}
