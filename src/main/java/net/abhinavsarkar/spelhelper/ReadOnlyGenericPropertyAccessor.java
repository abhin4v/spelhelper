/* Copyright 2010 Abhinav Sarkar <abhinav@abhinavsarkar.net>
 *
 * This file is a part of SpelHelper library.
 *
 * SpelHelper library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (GNU LGPL) as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * SpelHelper library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with SpelHelper library.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.abhinavsarkar.spelhelper;

import java.text.MessageFormat;

import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.PropertyAccessor;

abstract class ReadOnlyGenericPropertyAccessor implements
        PropertyAccessor {

    public final boolean canWrite(final EvaluationContext context,
            final Object target, final String name) throws AccessException {
        return false;
    }

    @SuppressWarnings("rawtypes")
    public final Class[] getSpecificTargetClasses() {
        return null;
    }

    public final void write(final EvaluationContext context, final Object target,
            final String name, final Object newValue) throws AccessException {
        throw new AccessException(MessageFormat.format(
                "Cannot write property: {0} of target: {1}", name, target));
    }

}