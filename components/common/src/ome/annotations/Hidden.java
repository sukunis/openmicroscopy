/*
 *   Copyright 2006 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package ome.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * annotation which specifies that a method parameter (e.g. a password) must be
 * hidden from logging output.
 * 
 * @author Josh Moore, josh.moore at gmx.de
 * @since 3.0-M3
 * @see <a href="https://trac.openmicroscopy.org/ome/ticket/209">ticket:209</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Hidden {
    // no fields
}
