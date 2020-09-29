/*
 * Designed and developed by 2020 skydoves (Jaewoong Eum)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.skydoves.landscapist.fresco

import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.preferredSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.test.filters.LargeTest
import androidx.ui.test.assertHeightIsAtLeast
import androidx.ui.test.assertIsDisplayed
import androidx.ui.test.assertWidthIsAtLeast
import androidx.ui.test.createComposeRule
import androidx.ui.test.onNodeWithTag
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
class FrescoImageTest {

  @get:Rule
  val composeTestRule = createComposeRule()

  @Test
  fun requestSuccess_withoutComposables() {
    composeTestRule.setContent {
      FrescoImage(
        imageUrl = IMAGE,
        modifier = Modifier
          .preferredSize(128.dp, 128.dp)
          .testTag(TAG_FRESCO),
        contentScale = ContentScale.Crop,
        observeLoadingProcess = false
      )
    }

    composeTestRule.onNodeWithTag(TAG_FRESCO)
      .assertIsDisplayed()
      .assertWidthIsAtLeast(128.dp)
      .assertHeightIsAtLeast(128.dp)
  }

  @Test
  fun requestSuccess_withLoadingComposables() {
    composeTestRule.setContent {
      FrescoImage(
        imageUrl = IMAGE,
        modifier = Modifier
          .preferredSize(128.dp, 128.dp)
          .testTag(TAG_FRESCO),
        contentScale = ContentScale.Crop,
        observeLoadingProcess = true,
        loading = {
          Box(modifier = Modifier.testTag(TAG_PROGRESS))
          composeTestRule.onNodeWithTag(TAG_PROGRESS)
            .assertIsDisplayed()
        }
      )
    }

    composeTestRule.onNodeWithTag(TAG_FRESCO)
      .assertIsDisplayed()
      .assertWidthIsAtLeast(128.dp)
      .assertHeightIsAtLeast(128.dp)
  }

  @Test
  fun requestSuccess_withSuccessComposables() {
    val state = ArrayList<FrescoImageState>()

    composeTestRule.setContent {
      FrescoImage(
        imageUrl = IMAGE,
        modifier = Modifier
          .preferredSize(128.dp, 128.dp)
          .testTag(TAG_FRESCO),
        contentScale = ContentScale.Crop,
        observeLoadingProcess = true,
        success = {
          state.add(it)
          assertThat(it.imageAsset, `is`(notNullValue()))
        },
        loading = {
          Box(modifier = Modifier.testTag(TAG_PROGRESS))

          composeTestRule.onNodeWithTag(TAG_PROGRESS)
            .assertIsDisplayed()
        }
      )
    }

    composeTestRule.onNodeWithTag(TAG_FRESCO)
      .assertIsDisplayed()
      .assertWidthIsAtLeast(128.dp)
      .assertHeightIsAtLeast(128.dp)

    composeTestRule.runOnIdle {
      assertThat(state.size, `is`(1))
      assertThat(state[0], instanceOf(FrescoImageState.Success::class.java))
    }
  }

  @Test
  fun requestSuccess_withFailureComposables() {
    val state = ArrayList<FrescoImageState>()

    composeTestRule.setContent {
      FrescoImage(
        imageUrl = "",
        modifier = Modifier
          .preferredSize(128.dp, 128.dp)
          .testTag(TAG_FRESCO),
        contentScale = ContentScale.Crop,
        observeLoadingProcess = true,
        failure = {
          Box(modifier = Modifier.testTag(TAG_ERROR))
          state.add(it)
        }
      )
    }

    composeTestRule.onNodeWithTag(TAG_ERROR)
      .assertIsDisplayed()
      .assertWidthIsAtLeast(128.dp)
      .assertHeightIsAtLeast(128.dp)

    composeTestRule.runOnIdle {
      assertThat(state.size, `is`(1))
      assertThat(state[0], instanceOf(FrescoImageState.Failure::class.java))
    }
  }
}