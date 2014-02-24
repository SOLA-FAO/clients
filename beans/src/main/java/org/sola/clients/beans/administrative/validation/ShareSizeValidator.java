/**
 * ******************************************************************************************
 * Copyright (C) 2014 - Food and Agriculture Organization of the United Nations (FAO).
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice,this list
 *       of conditions and the following disclaimer.
 *    2. Redistributions in binary form must reproduce the above copyright notice,this list
 *       of conditions and the following disclaimer in the documentation and/or other
 *       materials provided with the distribution.
 *    3. Neither the name of FAO nor the names of its contributors may be used to endorse or
 *       promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,PROCUREMENT
 * OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,STRICT LIABILITY,OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * *********************************************************************************************
 */
package org.sola.clients.beans.administrative.validation;

import java.util.List;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.sola.clients.beans.administrative.RrrShareBean;

/**
 * Validates total size of all shares in the given list of
 * {@link RrrShareBean}s.
 */
public class ShareSizeValidator implements ConstraintValidator<TotalShareSize, List<RrrShareBean>> {

    private float requiredTotalSize;

    public float getRequiredTotalSize() {
        return requiredTotalSize;
    }

    public void setRequiredTotalSize(float requiredTotalSize) {
        this.requiredTotalSize = requiredTotalSize;
    }

    @Override
    public void initialize(TotalShareSize a) {
        requiredTotalSize = a.shareSize();
    }

    @Override
    public boolean isValid(List<RrrShareBean> shareBeanList, ConstraintValidatorContext constraintContext) {
        if (shareBeanList == null || shareBeanList.size() < 1) {
            return true;
        }

        float totalSize = 0;
        boolean result = true;

        for (RrrShareBean shareBean : shareBeanList) {
            if (shareBean.getNominator() != null && shareBean.getDenominator() != null) {
                totalSize += (float) shareBean.getNominator() / shareBean.getDenominator();
            }
        }

        // Allow for inaccuracy of floating pt number calculation by multiplying 
        // by 10,000 (i.e 4dp).
        if (requiredTotalSize * 10000 != (Math.round(totalSize * 10000))) {
            result = false;
        }

        return result;
    }
}
