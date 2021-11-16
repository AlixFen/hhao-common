
/*
 * Copyright 2018-2021 WangSheng.
 *
 * Licensed under the GNU GENERAL PUBLIC LICENSE, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       https://www.gnu.org/licenses/gpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hhao.common.metadata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Locale;

/**
 * 货币组织代码，如CNY
 *
 * @author Wang
 * @since 1.0.0
 */
public class CurrencyUnitMetadata implements Metadata<CurrencyUnit> {
    protected final Logger logger = LoggerFactory.getLogger(CurrencyUnitMetadata.class);
    private final String NAME = "CURRENCY_UNIT";
    private CurrencyUnit currencyUnit = Monetary.getCurrency(Locale.CHINA);
    private String value="CNY";

    @Override
    public boolean support(String name) {
        if (name == null) {
            return false;
        }
        if (NAME.equals(name)) {
            return true;
        }
        return false;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public CurrencyUnit getMetadata() {
        return currencyUnit;
    }


    /**
     *
     * @param value 国际货币简码，如：CNY
     * @return
     */
    @Override
    public CurrencyUnit update(String value) {
        try {
            currencyUnit = Monetary.getCurrency(value);
            this.value=value;
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return currencyUnit;
    }

}

