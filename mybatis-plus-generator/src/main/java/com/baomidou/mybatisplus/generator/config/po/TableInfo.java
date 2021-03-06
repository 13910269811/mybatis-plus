/*
 * Copyright (c) 2011-2020, baomidou (jobob@qq.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.baomidou.mybatisplus.generator.config.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 表信息，关联到当前字段信息
 *
 * @author YangHu
 * @since 2016/8/30
 */
@Data
@Accessors(chain = true)
public class TableInfo {

    private final Set<String> importPackages = new HashSet<>();
    private boolean convert;
    private String name;
    private String comment;
    private String entityName;
    private String mapperName;
    private String xmlName;
    private String serviceName;
    private String serviceImplName;
    private String controllerName;
    private final List<TableField> fields = new ArrayList<>();
    private boolean havePrimaryKey;
    /**
     * 公共字段
     */
    private final List<TableField> commonFields = new ArrayList<>();
    private String fieldNames;

    public TableInfo setConvert(boolean convert) {
        this.convert = convert;
        return this;
    }

    protected TableInfo setConvert(StrategyConfig strategyConfig) {
        if (strategyConfig.startsWithTablePrefix(name) || strategyConfig.isEntityTableFieldAnnotationEnable()) {
            // 包含前缀
            this.convert = true;
        } else if (strategyConfig.isCapitalModeNaming(name)) {
            // 包含
            this.convert = false;
        } else {
            // 转换字段
            if (NamingStrategy.underline_to_camel == strategyConfig.getColumnNaming()) {
                // 包含大写处理
                if (StringUtils.containsUpperCase(name)) {
                    this.convert = true;
                }
            } else if (!entityName.equalsIgnoreCase(name)) {
                this.convert = true;
            }
        }
        return this;
    }

    public String getEntityPath() {
        return entityName.substring(0, 1).toLowerCase() + entityName.substring(1);
    }

    public TableInfo setEntityName(StrategyConfig strategyConfig, String entityName) {
        this.entityName = entityName;
        this.setConvert(strategyConfig);
        return this;
    }


    /**
     * @see #addFields(List)
     * @see #addFields(TableField...)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TableInfo setFields(List<TableField> fields) {
        return addFields(fields);
    }

    /**
     * @param fields 字段集合
     * @return this
     * @since 3.4.1
     */
    public TableInfo addFields(List<TableField> fields) {
        this.fields.addAll(fields);
        return this;
    }

    /**
     * @param fields 字段集合
     * @return this
     * @since 3.4.1
     */
    public TableInfo addFields(TableField... fields) {
        this.fields.addAll(Arrays.asList(fields));
        return this;
    }

    /**
     * @param pkg 包空间
     * @return this
     * @see #addImportPackages(String...)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TableInfo setImportPackages(String pkg) {
        importPackages.add(pkg);
        return this;
    }

    /**
     * @param pkgs 包空间
     * @return this
     * @since 3.4.1
     */
    public TableInfo addImportPackages(String... pkgs) {
        importPackages.addAll(Arrays.asList(pkgs));
        return this;
    }

    /**
     * 逻辑删除
     */
    public boolean isLogicDelete(String logicDeletePropertyName) {
        return fields.parallelStream().anyMatch(tf -> tf.getName().equals(logicDeletePropertyName));
    }

    /**
     * @param fieldNames fieldNames
     * @deprecated 3.4.1 不打算公开此方法了
     */
    @Deprecated
    public TableInfo setFieldNames(String fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    /**
     * 转换filed实体为 xml mapper 中的 base column 字符串信息
     */
    public String getFieldNames() {
        //TODO 感觉这个也啥必要,不打算公开set方法了
        if (StringUtils.isBlank(fieldNames)) {
            this.fieldNames = this.fields.stream().map(TableField::getColumnName).collect(Collectors.joining(", "));
        }
        return this.fieldNames;
    }

    /**
     * @param commonFields 公共字段
     * @return this
     * @see #addCommonFields(TableField...)
     * @see #addCommonFields(List)
     * @deprecated 3.4.1
     */
    @Deprecated
    public TableInfo setCommonFields(List<TableField> commonFields) {
        return addCommonFields(commonFields);
    }

    /**
     * 添加公共字段
     *
     * @param commonFields 公共字段
     * @return this
     * @since 3.4.1
     */
    public TableInfo addCommonFields(TableField... commonFields) {
        return addCommonFields(Arrays.asList(commonFields));
    }

    /**
     * 添加公共字段
     *
     * @param commonFields 公共字段
     * @return this
     * @since 3.4.1
     */
    public TableInfo addCommonFields(List<TableField> commonFields) {
        this.commonFields.addAll(commonFields);
        return this;
    }

    public void importPackage(StrategyConfig strategyConfig, GlobalConfig globalConfig){
        boolean importSerializable = true;
        if (StringUtils.isNotBlank(strategyConfig.getSuperEntityClass())) {
            // 自定义父类
            importSerializable = false;
            this.importPackages.add(strategyConfig.getSuperEntityClass());
        } else {
            if (globalConfig.isActiveRecord()) {
                // 无父类开启 AR 模式
                this.getImportPackages().add(com.baomidou.mybatisplus.extension.activerecord.Model.class.getCanonicalName());
            }
        }
        if (importSerializable) {
            this.importPackages.add(Serializable.class.getCanonicalName());
        }
        if (this.isConvert()) {
            this.importPackages.add(TableName.class.getCanonicalName());
        }
        if (strategyConfig.getLogicDeleteFieldName() != null && this.isLogicDelete(strategyConfig.getLogicDeleteFieldName())) {
            this.importPackages.add(TableLogic.class.getCanonicalName());
        }
        if (null != globalConfig.getIdType() && this.isHavePrimaryKey()) {
            // 指定需要 IdType 场景
            this.importPackages.add(IdType.class.getCanonicalName());
            this.importPackages.add(TableId.class.getCanonicalName());
        }
        this.fields.forEach(field -> {
            if (null != field.getColumnType() && null != field.getColumnType().getPkg()) {
                importPackages.add(field.getColumnType().getPkg());
            }
            if (field.isKeyFlag()) {
                // 主键
                if (field.isConvert() || field.isKeyIdentityFlag()) {
                    importPackages.add(TableId.class.getCanonicalName());
                }
                // 自增
                if (field.isKeyIdentityFlag()) {
                    importPackages.add(IdType.class.getCanonicalName());
                }
            } else if (field.isConvert()) {
                // 普通字段
                importPackages.add(TableField.class.getCanonicalName());
            }
            if (null != field.getFill()) {
                // 填充字段
                importPackages.add(TableField.class.getCanonicalName());
                importPackages.add(FieldFill.class.getCanonicalName());
            }
            String versionFieldName = strategyConfig.getVersionFieldName();
            if (StringUtils.isNotBlank(versionFieldName) && versionFieldName.equals(field.getName())) {
                this.importPackages.add(Version.class.getCanonicalName());
            }
        });
    }
}
