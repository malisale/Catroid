/*
 * Catroid: An on-device visual programming system for Android devices
 * Copyright (C) 2010-2018 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * An additional term exception under section 7 of the GNU Affero
 * General Public License, version 3, is available at
 * http://developer.catrobat.org/license_additional_term
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.catroid.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.R;
import org.catrobat.catroid.cast.CastManager;
import org.catrobat.catroid.common.Nameable;
import org.catrobat.catroid.content.Sprite;
import org.catrobat.catroid.content.bricks.Brick;
import org.catrobat.catroid.content.bricks.UserDefinedBrick;
import org.catrobat.catroid.content.bricks.brickspinner.StringOption;
import org.catrobat.catroid.ui.recyclerview.fragment.ScriptFragment;
import org.catrobat.catroid.utils.ToastUtil;
import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class AddUserBrickFragment extends Fragment {

	public static final String TAG = AddUserBrickFragment.class.getSimpleName();

	@SuppressWarnings("unused")
	private ScriptFragment scriptFragment;
	private UserDefinedBrick userDefinedBrick;
	private View userBrickView;
	private LinearLayout userBrickSpace;

	private MenuItem confirmItem;

	private Button addLabel;
	private Button addInput;

	public static AddUserBrickFragment newInstance(ScriptFragment scriptFragment) {
		AddUserBrickFragment fragment = new AddUserBrickFragment();

		fragment.scriptFragment = scriptFragment;
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_new_user_brick, container, false);
		userBrickSpace = view.findViewById(R.id.user_brick_space);

		addLabel = view.findViewById(R.id.button_add_label);
		addInput = view.findViewById(R.id.button_add_input);

		addLabel.setOnClickListener(v -> handleAddLabel());
		addInput.setOnClickListener(v -> handleAddInput());

		Bundle arguments = getArguments();
		if (arguments != null) {
			userDefinedBrick =
					(UserDefinedBrick) getArguments().getSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT);
			if (userDefinedBrick != null) {
				userBrickView = userDefinedBrick.getView(getActivity());
				userBrickSpace.addView(userBrickView);
			}
		}

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(R.string.brick_add_new_user_brick);
			}
		}

		setHasOptionsMenu(true);

		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();

		AppCompatActivity activity = (AppCompatActivity) getActivity();
		if (activity != null) {
			ActionBar actionBar = activity.getSupportActionBar();
			if (actionBar != null) {
				actionBar.setTitle(R.string.category_user_bricks);
			}
		}
	}

	@Override
	public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.menu_confirm, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public void onPrepareOptionsMenu(@NonNull Menu menu) {
		for (int index = 0; index < menu.size(); index++) {
			menu.getItem(index).setVisible(false);
		}

		confirmItem = menu.findItem(R.id.confirm);
		confirmItem.setVisible(true);
		confirmItem.setEnabled(true);
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.confirm) {
			Sprite currentSprite = ProjectManager.getInstance().getCurrentSprite();
			boolean brickIsEmpty = userDefinedBrick.isEmpty();

			if (brickIsEmpty) {
				userDefinedBrick.addLabel(new StringOption(""));
			}

			if (Sprite.doesUserBrickAlreadyExist(userDefinedBrick, currentSprite)) {
				ToastUtil.showErrorWithColor(getContext(), R.string.brick_user_defined_already_exists, Color.RED);
				if (brickIsEmpty) {
					userDefinedBrick.removeLastLabel();
				}
			} else {
				currentSprite.addUserDefinedBrick(userDefinedBrick);
				addBrickToScript(userDefinedBrick);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	private void addBrickToScript(Brick brickToAdd) {
		if ((ProjectManager.getInstance().getCurrentProject().isCastProject())
				&& CastManager.unsupportedBricks.contains(brickToAdd.getClass())) {
			ToastUtil.showError(getActivity(), R.string.error_unsupported_bricks_chromecast);
			return;
		}

		try {
			//TODO: CATROID-218
			brickToAdd = brickToAdd.clone();
			//scriptFragment.addBrick(brickToAdd);

			FragmentManager fragmentManager = getFragmentManager();
			if (fragmentManager != null) {
				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

				Fragment categoryFragment = getFragmentManager()
						.findFragmentByTag(BrickCategoryFragment.BRICK_CATEGORY_FRAGMENT_TAG);

				if (categoryFragment != null) {
					fragmentTransaction.remove(categoryFragment);
					getFragmentManager().popBackStack();
				}

				Fragment userBrickListFragment = getFragmentManager().findFragmentByTag(UserBrickListFragment.USER_BRICK_LIST_FRAGMENT_TAG);
				if (userBrickListFragment != null) {
					fragmentTransaction.remove(userBrickListFragment);
					getFragmentManager().popBackStack();
				}

				Fragment addUserBrickFragment =
						getFragmentManager().findFragmentByTag(AddUserBrickFragment.TAG);

				if (addUserBrickFragment != null) {
					fragmentTransaction.remove(addUserBrickFragment);
					getFragmentManager().popBackStack();
				}

				fragmentTransaction.commit();
			}
		} catch (CloneNotSupportedException e) {
			Log.e(getTag(), e.getLocalizedMessage());
			ToastUtil.showError(getActivity(), R.string.error_adding_brick);
		}
	}

	private void handleAddLabel() {
		AddUserDataToUserBrickFragment addUserDataToUserBrickFragment = new AddUserDataToUserBrickFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT, userDefinedBrick);
		bundle.putBoolean(UserDefinedBrick.ADD_INPUT_OR_LABEL_BUNDLE_ARGUMENT, UserDefinedBrick.LABEL);

		addUserDataToUserBrickFragment.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager != null) {
			fragmentManager.beginTransaction()
					.add(R.id.fragment_container, addUserDataToUserBrickFragment, AddUserDataToUserBrickFragment.TAG)
					.addToBackStack(AddUserDataToUserBrickFragment.TAG)
					.commit();
		}
	}

	private void handleAddInput() {
		AddUserDataToUserBrickFragment addUserDataToUserBrickFragment = new AddUserDataToUserBrickFragment();

		Bundle bundle = new Bundle();
		bundle.putSerializable(UserDefinedBrick.USER_BRICK_BUNDLE_ARGUMENT, userDefinedBrick);
		bundle.putBoolean(UserDefinedBrick.ADD_INPUT_OR_LABEL_BUNDLE_ARGUMENT, UserDefinedBrick.INPUT);

		addUserDataToUserBrickFragment.setArguments(bundle);

		FragmentManager fragmentManager = getFragmentManager();
		if (fragmentManager != null) {
			fragmentManager.beginTransaction()
					.add(R.id.fragment_container, addUserDataToUserBrickFragment, AddUserDataToUserBrickFragment.TAG)
					.addToBackStack(AddUserDataToUserBrickFragment.TAG)
					.commit();
		}
	}

	void addUserDataToUserBrick(Nameable input, boolean isInputOrLabel) {
		if (isInputOrLabel) {
			userDefinedBrick.addInput(input);
		} else {
			userDefinedBrick.addLabel(input);
		}
		updateBrickView();
	}

	private void updateBrickView() {
		userBrickSpace.removeView(userBrickView);
		userBrickView = userDefinedBrick.getView(getActivity());
		userBrickSpace.addView(userBrickView);
	}
}
