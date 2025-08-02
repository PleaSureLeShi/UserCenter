// import { GithubOutlined } from '@ant-design/icons';
import {DefaultFooter} from '@ant-design/pro-components';
import {CSDN_LINK} from "@/constants";

const Footer: React.FC = () => {
  const defaultMessage = '杭州师范大学-信息科学与技术学院-软工222-高_潮_园_子-出品';
  const currentYear = new Date().getFullYear();
  return (
    <DefaultFooter
      copyright={`${currentYear} ${defaultMessage}`}
      links={[
        {
          key: 'CSDN',
          title: 'CSDN主页',
          href: CSDN_LINK,
          blankTarget: true,
        },
      ]}
    />
  );
};
export default Footer;
