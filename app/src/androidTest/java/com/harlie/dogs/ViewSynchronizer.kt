package com.harlie.dogs

// credit: https://stackoverflow.com/a/50218725/2840837

import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.util.TreeIterables
import org.hamcrest.Matcher
import java.util.concurrent.CountDownLatch

object ViewSynchronizer {

    @Throws(InterruptedException::class)
    fun viewExists(viewMatcher: Matcher<View?>, millis: Long): Boolean {
        val found = arrayOfNulls<Boolean>(1)
        val latch = CountDownLatch(1)
        val action: ViewAction = object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isRoot()
            }

            override fun getDescription(): String {
                return "wait for a specific view with id <" + viewMatcher.toString()
                    .toString() + "> during " + millis.toString() + " millis."
            }

            override fun perform(uiController: UiController, view: View?) {
                uiController.loopMainThreadUntilIdle()
                val startTime = System.currentTimeMillis()
                val endTime = startTime + millis
                do {
                    for (child in TreeIterables.breadthFirstViewTraversal(view)) {
                        if (viewMatcher.matches(child)) {
                            System.out.println("perform: found match")
                            found[0] = true
                            latch.countDown()
                            return
                        }
                    }
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)
                found[0] = false
                latch.countDown()
            }
        }
        onView(isRoot()).perform(action)
        latch.await()
        return found[0]!!
    }

}