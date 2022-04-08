package com.thatmg393.esmanager.fragments.createmodfragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.thatmg393.esmanager.CreateModActivity;
import com.thatmg393.esmanager.MainActivity;
import com.thatmg393.esmanager.R;
import com.thatmg393.esmanager.Utils;
import com.thatmg393.esmanager.adapters.TextAdapter;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProjectExplorerFragment extends Fragment {

    private static final int[] ctxMenuItems = {Menu.FIRST, Menu.FIRST + 1, Menu.FIRST + 2, Menu.FIRST + 3};
    // private static final Vector<Button> buttons = new Vector<>();

    private static final String rootPath = CreateModActivity.pp;

    private TextAdapter textAdapter;
    private File[] modPathFilesnFolder;

    private List<String> pathLists;
    private ListView explorer;

    private boolean isOneItemSelected = false;
    private boolean isMultipleItemSelected = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_project_explorer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        explorer = requireView().findViewById(R.id.project_explorer);
        textAdapter = new TextAdapter();
        textAdapter.setData(pathLists);

        if (MainActivity.sharedPreferencesUtil.getString("shouldUseCM").equals("DEFAULT")) {
            Utils.LoggerUtils.logWarn("\"shouldUseCM\" returned \"DEFAULT\" which should not happen in normal circumstances.");
        } else if (Boolean.parseBoolean(MainActivity.sharedPreferencesUtil.getString("shouldUseCM"))) {
            registerForContextMenu(explorer);
        }

        try {
            if (Utils.ActivityUtils.arePermissionsDenied(getActivity().getApplicationContext(), Utils.app_perms)) {
                /* Test code
                new FragmentPermissionHelper().requestMultiPermission(getActivity(), new FragmentPermissionMultipleInterface() {
                    @Override
                    public void isGrantedMultiple(Map<String, Boolean> isGranted) {
                        explorerInit();
                    }
                }, Utils.app_perms);
                 */

                explorerInit();
            } else {
                explorerInit();
            }
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    @Override
    public void onCreateContextMenu(@NotNull ContextMenu menu, @NotNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        String header = textAdapter.getItem(info.position).substring(textAdapter.getItem(info.position).lastIndexOf('/') + 1);
        header = header.substring(0, 1).toUpperCase();

        menu.setHeaderTitle(header);

        menu.add(1, ctxMenuItems[0], ctxMenuItems[0], "Delete");
        menu.add(1, ctxMenuItems[1], ctxMenuItems[1], "Copy");
        menu.add(1, ctxMenuItems[2], ctxMenuItems[2], "Move");

        if (isOneItemSelected && !isMultipleItemSelected) {
            menu.add(1, ctxMenuItems[3], ctxMenuItems[3], "Rename");
        } else {
            menu.removeItem(ctxMenuItems[3]);
        }
    }

    // AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Toast.makeText(getContext(), "Delete", Toast.LENGTH_SHORT).show();
                return true;

            case 2:
                Toast.makeText(getContext(), "Copy", Toast.LENGTH_SHORT).show();
                return true;

            case 3:
                Toast.makeText(getContext(), "Move", Toast.LENGTH_SHORT).show();
                return true;

            case 4:
                Toast.makeText(getContext(), "Rename", Toast.LENGTH_SHORT).show();
                return true;

        }
        return false;
    }

    private void explorerInit() {
        Executor executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            File modRootDir = new File(rootPath);

            modPathFilesnFolder = modRootDir.listFiles();

            pathLists = new ArrayList<>();

            for (File file : modPathFilesnFolder) {
                pathLists.add(file.getAbsolutePath());
            }

            handler.post(() -> {
                explorer.setAdapter(textAdapter);

                boolean[] selection = new boolean[pathLists.size()];

                explorer.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        selection[position] = !selection[position];
                        textAdapter.setSelection(selection);

                        if (!MainActivity.sharedPreferencesUtil.getBoolean("shouldUseCM")) {
                            isOneItemSelected = false;
                            isMultipleItemSelected = false;

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
                                    if (s > 1) {
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
                        }

                        return false;
                    }
                }); // onItemLongClickListener
            }); // handler.post()
        }); // executor.execute()
    }

    private void deleteFolderOrFile(File fileToDelete) {
        if (fileToDelete.isDirectory()) {
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

    private void PickFile() {
        /* Disabled for some reason.
        SingleFilePickerDialog singleFilePickerDialog = new SingleFilePickerDialog(getApplicationContext(),
                () -> Toast.makeText(getApplicationContext(), "Canceled!!", Toast.LENGTH_SHORT).show(),
                files -> Toast.makeText(getApplicationContext(), files[0].getPath(), Toast.LENGTH_SHORT).show());
        singleFilePickerDialog.show();
         */
    }

    /*
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
                 */

}
