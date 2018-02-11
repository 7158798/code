
/**
* @Title: TaskExecutionResult.java
* @Package com.pay.card.model
* @Description: TODO(用一句话描述该文件做什么)
* @author jing.jin
* @date 2017年12月20日
* @version V1.0
*/

package com.pay.card.model;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @ClassName: TaskExecutionResult
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author jing.jin
 * @date 2017年12月20日
 *
 */
@Table(name = "credit_task_result")
@Entity
public class TaskExecutionResult extends BaseEntity {
    /**
     * 执行的服务器
     */
    private String host;
    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务名称
     */
    private String exeDescription;

    /**
     * @return exeDescription
     */

    public String getExeDescription() {
        return exeDescription;
    }

    /**
     * @return host
     */

    public String getHost() {
        return host;
    }

    /**
     * @return taskName
     */

    public String getTaskName() {
        return taskName;
    }

    /**
     * @param exeDescription
     *            the exeDescription to set
     */

    public void setExeDescription(String exeDescription) {
        this.exeDescription = exeDescription;
    }

    /**
     * @param host
     *            the host to set
     */

    public void setHost(String host) {
        this.host = host;
    }

    /**
     * @param taskName
     *            the taskName to set
     */

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

}
