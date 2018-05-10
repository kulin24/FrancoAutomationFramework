
package org.openqa.selenium.support;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to mark a field on a Page Object to indicate an alternative mechanism
 * for locating the element or a list of elements. Used in conjunction with
 * {@link org.openqa.selenium.support.PageFactory} this allows users to quickly
 * and easily create PageObjects.
 * 
 * It can be used on a types as well, but will not be processed by default.
 * 
 * <p>
 * You can either use this annotation by specifying both "how" and "using" or by
 * specifying one of the location strategies (eg: "id") with an appropriate
 * value to use. Both options will delegate down to the matching
 * {@link org.openqa.selenium.By} methods in By class.
 * 
 * For example, these two annotations point to the same element:
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	&#064;FindBy(id = &quot;foobar&quot;)
 * 	WebElement foobar;
 * 	&#064;FindBy(how = How.ID, using = &quot;foobar&quot;)
 * 	WebElement foobar;
 * }
 * </pre>
 * 
 * and these two annotations point to the same list of elements:
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	&#064;FindBy(tagName = &quot;a&quot;)
 * 	List&lt;WebElement&gt; links;
 * 	&#064;FindBy(how = How.TAG_NAME, using = &quot;a&quot;)
 * 	List&lt;WebElement&gt; links;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface FindBy {
	How how() default How.UNSET;

	String using() default "";

	String id() default "";

	String name() default "";

	String className() default "";

	String css() default "";

	String tagName() default "";

	String linkText() default "";

	String partialLinkText() default "";

	String xpath() default "";

	String locator() default "";
}
